package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.io.File;
import java.lang.reflect.Method;


public class MyFileAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This File file) {
            throw new SecurityException("Access denied");

    }

}