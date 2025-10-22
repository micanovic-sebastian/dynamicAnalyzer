package bachelor.test;

import net.bytebuddy.asm.Advice;
import java.lang.reflect.Method;

public class MyClassLoaderAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) String className) {
                throw new SecurityException("Access denied");

    }
}