package bachelor.advice;

import net.bytebuddy.asm.Advice;
import java.lang.reflect.Executable;

public class BlockingAdvice {

    // Hier werde alle Methoden vom Java Agent abgefangen, Ausführung wird beendet
    // und stattdessen eine Fehlermeldung in der Konsole ausgegeben
    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Executable executable) {
        String name = executable.getDeclaringClass().getName() + "." + executable.getName();
        throw new SecurityException("Call to " + name + " denied by agent configuration!");
    }
}