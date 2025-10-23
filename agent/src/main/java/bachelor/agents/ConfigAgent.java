package bachelor.agents;

import bachelor.config.AgentConfiguration;
import bachelor.config.ConfigLoader;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.matcher.ElementMatchers.*;

/**
 * Hauptklasse des Java-Agenten
 * Hier werden Klassen zur Laufzeit instrumentiert und
 * Aufrufe anhand der Konfigurationsdatei blockiert
 */
public class ConfigAgent {

    private static final Logger logger = LogManager.getLogger(ConfigAgent.class);

    private static final String GENERIC_ADVICE_CLASS = "bachelor.advice.BlockingAdvice";

    // Bestimmte JDK-Kernklassen müssen neugeladen werden, weil diese vom Bootstrap-Classloader geladen werden
    private static List<String> classesToRetransform = Arrays.asList(
            "java.io.File", "java.nio.file.Files", "java.lang.reflect.Method",
            "java.io.OutputStream", "java.io.Writer", "java.io.InputStream",
            "java.net.Socket", "java.lang.Runtime", "java.lang.ProcessBuilder",
            "java.lang.Class", "java.lang.System"
    );

    /**
     * Die premain-Methode ist der Einsprungpunkt für den Java-Agenten
     * @param agentArguments Argumente die dem Agenten übergeben werden (hier der Pfad zur config)
     * @param instrumentation Das Instrumentation-API-Objekt von der JVM
     */
    public static void premain(String agentArguments, Instrumentation instrumentation) {

        logger.info("Starting JABS Agent...");

        // Parameter parsen
        AgentConfiguration config = ConfigLoader.loadConfig(agentArguments);

        // ByteBuddy und Abhängigkeiten von ByteBuddy müssen ignoriert werden
        AgentBuilder builder = new AgentBuilder.Default()
                .ignore(ElementMatchers.nameStartsWith("java.util.concurrent"))
                .ignore(ElementMatchers.nameStartsWith("net.bytebuddy"))
                .ignore(ElementMatchers.nameStartsWith("com.fasterxml.jackson")) // Ignore parse
                .ignore(ElementMatchers.nameStartsWith("org.apache.logging.log4j")) // *** Ignore Log4j2 ***
                .with(RETRANSFORMATION)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE);
        // Ist für Debugging sehr nützlich, die Meldungen sind aber für den Normalbetrieb zu detailliert
        //      .with(AgentBuilder.Listener.StreamWriting.toSystemError());

        // Es können auch Packages, Klassen und natürlich auch Methoden blockiert werden
        // Die Daten werden hier aus der JSON-Datei ausgelesen dem AgentBuildeer übergeben
        if (!config.getBlockedPackages().isEmpty()) {
            ElementMatcher.Junction<TypeDescription> packageMatcher = ElementMatchers.none();
            for (String pkg : config.getBlockedPackages()) {
                logger.info("Blocking package: {}", pkg);
                packageMatcher = packageMatcher.or(nameStartsWith(pkg + "."));
            }
            builder = builder.type(packageMatcher.and(not(isInterface())))
                    .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(ConfigAgent.class.getClassLoader())
                            .advice(isMethod().or(isConstructor()), GENERIC_ADVICE_CLASS));
        }

        // Und hier für Klassen
        if (!config.getBlockedClasses().isEmpty()) {
            ElementMatcher.Junction<TypeDescription> classMatcher = ElementMatchers.none();
            for (String cls : config.getBlockedClasses()) {
                logger.info("Blocking class: {}", cls);
                classMatcher = classMatcher.or(named(cls));
            }
            builder = builder.type(classMatcher.and(not(isInterface())))
                    .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(ConfigAgent.class.getClassLoader())
                            .advice(isMethod().or(isConstructor()), GENERIC_ADVICE_CLASS));
        }

        // Hier werden auch die einzelnen Methoden abgefangen
        for (String methodSignature : config.getBlockedMethods()) {
            logger.info("Blocking method: {}", methodSignature);
            try {
                // Nach dem letzten Punkt kommt der Methodenname, z.B. bei java.lang.reflect.Method.invoke
                // ist invoke der Methodenname
                int lastDot = methodSignature.lastIndexOf('.');
                if (lastDot == -1 || lastDot == methodSignature.length() - 1) {
                    logger.error("### Agent: Invalid method signature in config: {}", methodSignature);
                    continue;
                }
                String className = methodSignature.substring(0, lastDot);
                String methodName = methodSignature.substring(lastDot + 1);

                builder = builder.type(named(className))
                        .transform(new AgentBuilder.Transformer.ForAdvice()
                                .include(ConfigAgent.class.getClassLoader())
                                .advice(named(methodName), GENERIC_ADVICE_CLASS));

            } catch (Exception e) {
                logger.error("### Agent: Error processing method signature '{}'", methodSignature, e);
            }
        }

        // Agent wird hiermit endglültig installiert
        builder.installOn(instrumentation);
        logger.info("Agent installed.");

        // Hier werden die JDK-Kernklassen die bereits vom Bootstrap-Classloader geladen wurden,
        // nochmal geladen
        try {
            logger.debug("Attempting to retransform core JDK classes...");
            for (String clazzString : classesToRetransform) {
                try {
                    Class<?> clazz = Class.forName(clazzString);
                    instrumentation.retransformClasses(clazz);
                    logger.debug("Retransformed: {}", clazzString);
                } catch (ClassNotFoundException e) {
                    // Die Klassen müssen nicht unbedingt vom Bootstrap-Classloader geladen werden
                    // wenn diese im Code der zu analysieren ist nicht vorkommen, und Fehlerbehandlung ist in diesem Fall
                    // auch nicht möglich
                    logger.trace("Class not found for retransformation, skipping: {}", clazzString);
                }
            }
            logger.debug("Core JDK class retransformation complete.");
        } catch (Exception e) {
            logger.error("Error during retransformation request", e);
        }
    }
}