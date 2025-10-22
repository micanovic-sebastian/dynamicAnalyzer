package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;
import java.net.SocketAddress;

public class MyNetworkAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) SocketAddress address) {
        throw new SecurityException("Network connection denied by agent!");
    }
}