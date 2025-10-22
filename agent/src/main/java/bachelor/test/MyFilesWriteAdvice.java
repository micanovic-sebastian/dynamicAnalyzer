package bachelor.test;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MyFilesWriteAdvice {

    private static final List<String> LINUX_SENSITIVE_PATHS = Arrays.asList(
        "/etc/",
        "/root/",
        "/bin/",
        "/sbin/",
        "/usr/bin/",
        "/var/log/",
        "/home/",
        "/proc/",
        "/sys/",
        "/dev/"
    );

    // Windows sensitive paths
    private static final List<String> WINDOWS_SENSITIVE_PATHS = Arrays.asList(
        "C:\\Windows\\",
        "C:\\Program Files\\",
        "C:\\Program Files (x86)\\",
        "C:\\Users\\",
        "C:\\Documents and Settings\\",
        "C:\\System32\\",
        "C:\\$Recycle.Bin\\"
    );

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) java.nio.file.Path path, @Advice.Argument(1) byte[] bytes) {
        throw new SecurityException("Access denied");


    }

    private static boolean containsSensitivePath(String pathToCheck) {
        for (String path : LINUX_SENSITIVE_PATHS) {
            if (pathToCheck.contains(path)) return true;
        }
        for (String path : WINDOWS_SENSITIVE_PATHS) {
            if (pathToCheck.contains(path)) return true;
        }
        return false;
    }
}
