package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.io.Writer;
import java.lang.reflect.Method;

public class MyWriterAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This Writer writer, @Advice.Argument(0) char[] buffer) {
               throw new SecurityException("Access denied");

    }
}