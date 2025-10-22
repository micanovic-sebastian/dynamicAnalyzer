package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class MyMethodInvokeAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This Method targetMethod, @Advice.Argument(0) Object instance) {
        throw new SecurityException("Access denied");

    }
}