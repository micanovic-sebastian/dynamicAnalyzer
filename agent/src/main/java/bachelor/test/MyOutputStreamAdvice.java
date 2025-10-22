package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.io.OutputStream;
import java.lang.reflect.Method;

public class MyOutputStreamAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This OutputStream os, @Advice.Argument(0) byte[] buffer) {
             throw new SecurityException("Access denied");

    }
}