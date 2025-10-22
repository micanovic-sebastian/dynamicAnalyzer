package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;
import java.nio.file.Path;

public class MyFilePermissionAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) Path path) {
        throw new SecurityException("Access denied");
    }

}
