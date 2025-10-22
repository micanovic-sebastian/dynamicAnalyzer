package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class MyCipherAdvice {

    private int numOfCalls;

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) String transformation) {
        throw new SecurityException("Cryptographic operations denied");
    }
}