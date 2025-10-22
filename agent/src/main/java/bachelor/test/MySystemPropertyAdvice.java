package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class MySystemPropertyAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) String key) {
        throw new SecurityException("Access to sensitive system property '" + key + "' denied by agent!");
    }
}