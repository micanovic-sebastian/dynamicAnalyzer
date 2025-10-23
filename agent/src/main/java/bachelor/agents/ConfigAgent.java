package bachelor.agents;

import bachelor.config.AgentConfiguration;
import bachelor.config.ConfigLoader;
import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.description.type.TypeDescription;
import net.bytebuddy.matcher.ElementMatcher;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class ConfigAgent {

    // A single, generic advice class
    private static final String GENERIC_ADVICE_CLASS = "bachelor.advice.GenericBlockAdvice";

    // Bestimmte JDK-Kernklassen müssen werden, weil diese vom Bootstrap-Classloader geladen werden
    // und der Java Agent
    private static List<String> classesToRetransform = Arrays.asList(
            "java.io.File", "java.nio.file.Files", "java.lang.reflect.Method",
            "java.io.OutputStream", "java.io.Writer", "java.io.InputStream",
            "java.net.Socket", "java.lang.Runtime", "java.lang.ProcessBuilder",
            "java.lang.Class", "java.lang.System"
    );

    public static void premain(String agentArguments, Instrumentation instrumentation) throws IOException {

        // agentArguments is the path to config.json
        AgentConfiguration config = ConfigLoader.loadConfig(agentArguments);

        // Start building the AgentBuilder
        AgentBuilder builder = new AgentBuilder.Default()
                .ignore(ElementMatchers.nameStartsWith("java.util.concurrent"))
                .ignore(ElementMatchers.nameStartsWith("net.bytebuddy")) // Ignore self
                .ignore(ElementMatchers.nameStartsWith("com.fasterxml.jackson")) // Ignore parser
                .with(RETRANSFORMATION)
                .with(AgentBuilder.TypeStrategy.Default.REDEFINE);
//                .with(AgentBuilder.Listener.StreamWriting.toSystemError()); // Uncomment for debugging

        // 1. Block Packages
        // This blocks *all methods* in *all classes* starting with the package name.
        if (!config.getBlockedPackages().isEmpty()) {
            ElementMatcher.Junction<TypeDescription> packageMatcher = ElementMatchers.none();
            for (String pkg : config.getBlockedPackages()) {
                System.out.println(pkg);
                packageMatcher = packageMatcher.or(nameStartsWith(pkg + "."));
            }
            builder = builder.type(packageMatcher.and(not(isInterface())))
                    .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(ConfigAgent.class.getClassLoader())
                            .advice(isMethod().or(isConstructor()), GENERIC_ADVICE_CLASS));
        }

        // 2. Block Classes
        // This blocks *all methods* in a specific class.
        if (!config.getBlockedClasses().isEmpty()) {
            ElementMatcher.Junction<TypeDescription> classMatcher = ElementMatchers.none();
            for (String cls : config.getBlockedClasses()) {
                System.out.println(cls);
                classMatcher = classMatcher.or(named(cls));
            }
            builder = builder.type(classMatcher.and(not(isInterface())))
                    .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(ConfigAgent.class.getClassLoader())
                            .advice(isMethod().or(isConstructor()), GENERIC_ADVICE_CLASS));
        }

        // 3. Block Specific Methods
        // This blocks a single, named method in a named class.
        for (String methodSignature : config.getBlockedMethods()) {
            System.out.println(methodSignature);
            try {
                int lastDot = methodSignature.lastIndexOf('.');
                if (lastDot == -1 || lastDot == methodSignature.length() - 1) {
                    System.err.println("### Agent: Invalid method signature in config: " + methodSignature);
                    continue;
                }
                String className = methodSignature.substring(0, lastDot);
                String methodName = methodSignature.substring(lastDot + 1);

                builder = builder.type(named(className))
                        .transform(new AgentBuilder.Transformer.ForAdvice()
                                .include(ConfigAgent.class.getClassLoader())
                                .advice(named(methodName), GENERIC_ADVICE_CLASS));

            } catch (Exception e) {
                System.err.println("### Agent: Error processing method signature '" + methodSignature + "': " + e.getMessage());
            }
        }

        // Finally, install the agent
        builder.installOn(instrumentation);

        // Retransformation logic remains the same (and is still important)
        try {
            for (String clazzString : classesToRetransform) {
                try {
                    Class<?> clazz = Class.forName(clazzString);
                    instrumentation.retransformClasses(clazz);
                } catch (ClassNotFoundException e) {
                    // Ignore: Class might not be used, e.g. sun.nio.ch.ChannelOutputStream
                }
            }
        } catch (Exception e) {
            System.err.println("Error during retransformation request: " + e.getMessage());
            e.printStackTrace();
        }
    }
}