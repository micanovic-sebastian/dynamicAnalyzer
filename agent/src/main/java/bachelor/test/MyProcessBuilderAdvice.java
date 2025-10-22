package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class MyProcessBuilderAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method) {
        throw new SecurityException("Process creation denied by agent!");
    }
}