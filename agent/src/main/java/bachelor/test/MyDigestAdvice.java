package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class MyDigestAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) String algorithm) {
        throw new SecurityException("Hash functions are not allowed");
    }
}