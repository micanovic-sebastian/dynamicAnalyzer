package bachelor.advice;

import net.bytebuddy.asm.Advice;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.lang.reflect.Executable;

/**
 * ByteBuddy Advice-Klasse
 * Sie enthält die Logik die ausgeführt wird wenn eine blockierte Methode abgefangen wird
 */
public class BlockingAdvice {

    private static final Logger logger = LogManager.getLogger(BlockingAdvice.class);

    // Hier werde alle Packages, Klassen und Methoden abgefangen die in der Konfigurationsdatei stehen, Ausführung wird beendet
    // und stattdessen eine Fehlermeldung in der Konsole ausgegeben

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Executable executable) {
        String name = executable.getDeclaringClass().getName() + "." + executable.getName();

        logger.warn("BLOCKED -> Call to {} denied by agent configuration!", name);

        throw new SecurityException("Call to " + name + " denied by agent configuration!");
    }
}