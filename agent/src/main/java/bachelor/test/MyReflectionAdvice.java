package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class MyReflectionAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This Class<?> clazz, @Advice.Argument(0) String name) {
                throw new SecurityException("Access denied");

    }
}