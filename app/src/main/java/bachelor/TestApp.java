package bachelor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.FileWriter;
import java.net.DatagramPacket;
import java.net.DatagramSocket;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.Socket;
import java.security.MessageDigest;
import javax.crypto.Cipher;

/**
 * A comprehensive test application to verify the JABS agent's blocking capabilities.
 */
public class TestApp {
    public static void main(String[] args) {


        // --- Original 7 Tests ---
        // Test 1: Runtime.exec()
        testExec(">>> Test 1: Attempting Runtime.exec()... ");

        // Test 2: ProcessBuilder.start()
        testProcessBuilder(">>> Test 2: Attempting ProcessBuilder.start()... ");

        // Test 3: Sensitive File Write (NIO)
        testFileWrite(">>> Test 3: Attempting to write to sensitive path (NIO)... ");

        // Test 4: Network Connection
        testNetwork(">>> Test 4: Attempting network connection (Socket.connect)... ");

        // Test 5: Reflection (Method.invoke)
        testReflection(">>> Test 5: Attempting Method.invoke()... ");

        // Test 6: Dynamic Class Loading
        testClassLoader(">>> Test 6: Attempting to load a forbidden class (Class.forName)... ");

        // Test 7: Sensitive System Property
        testSystemProperty(">>> Test 7: Attempting to access a sensitive property (System.getProperty)... ");

        // --- 30 New Tests ---

        // --- File I/O (java.io.File) ---
        testFileCreateNewFile(">>> Test 8: Attempting java.io.File.createNewFile... ");
        testFileMkdir(">>> Test 9: Attempting java.io.File.mkdir... ");
        testFileDelete(">>> Test 10: Attempting java.io.File.delete... ");
        testFileRenameTo(">>> Test 11: Attempting java.io.File.renameTo... ");

        // --- File I/O (Stream Classes) ---
        testFileInputStream(">>> Test 12: Attempting new FileInputStream... ");
        testFileOutputStream(">>> Test 13: Attempting new FileOutputStream... ");
        testFileWriter(">>> Test 14: Attempting new FileWriter... ");

        // --- File I/O (java.nio.file.Files) ---
        testFilesReadAllBytes(">>> Test 15: Attempting java.nio.file.Files.readAllBytes... ");
        testFilesReadString(">>> Test 16: Attempting java.nio.file.Files.readString... ");
        testFilesNewInputStream(">>> Test 17: Attempting java.nio.file.Files.newInputStream... ");
        testFilesNewOutputStream(">>> Test 18: Attempting java.nio.file.Files.newOutputStream... ");
        testFilesCreateFile(">>> Test 19: Attempting java.nio.file.Files.createFile... ");
        testFilesCreateDirectory(">>> Test 20: Attempting java.nio.file.Files.createDirectory... ");
        testFilesNioDelete(">>> Test 21: Attempting java.nio.file.Files.delete... ");
        testFilesMove(">>> Test 22: Attempting java.nio.file.Files.move... ");
        testFilesCopy(">>> Test 23: Attempting java.nio.file.Files.copy... ");

        // --- Reflection ---
        testClassGetConstructor(">>> Test 24: Attempting java.lang.Class.getConstructor... ");
        testClassGetField(">>> Test 25: Attempting java.lang.Class.getField... ");
        testConstructorNewInstance(">>> Test 26: Attempting java.lang.reflect.Constructor.newInstance... ");
        testReflectionSetAccessible(">>> Test 27: Attempting java.lang.reflect.Method.setAccessible... ");

        // --- Networking ---
        testUrlOpenConnection(">>> Test 28: Attempting java.net.URL.openConnection... ");
        testUrlOpenStream(">>> Test 29: Attempting java.net.URL.openStream... ");
        testServerSocketBind(">>> Test 30: Attempting java.net.ServerSocket.bind... ");
        testDatagramSocketSend(">>> Test 31: Attempting java.net.DatagramSocket.send... ");

        // --- System & Runtime ---
        testSystemExit(">>> Test 32: Attempting java.lang.System.exit... ");
        testSystemGetProperties(">>> Test 33: Attempting java.lang.System.getProperties... ");
        testSystemGetEnv(">>> Test 34: Attempting java.lang.System.getenv... ");
        testRuntimeLoad(">>> Test 35: Attempting java.lang.Runtime.load... ");

        // --- Cryptography (Blocked by package) ---
        testCryptoCipher(">>> Test 36: Attempting javax.crypto.Cipher.getInstance... ");
        testCryptoMessageDigest(">>> Test 37: Attempting java.security.MessageDigest.getInstance... ");

        System.out.println("--- JABS Comprehensive Test Suite Finished ---");
    }

    // --- Utility Runner ---
    private static void runTest(String description, Runnable testLogic) {
        System.out.print(description);
        try {
            testLogic.run();
            System.out.println("[ FAILED ] Sandbox Bypassed!");
        } catch (Throwable t) {
            // We expect a SecurityException or a specific exception wrapping it.
            System.out.println("[ BLOCKED ] " + t.getClass().getName() + ": " + t.getMessage());
        }
    }

    // --- Original 7 Test Implementations ---
    private static void testExec(String desc) {
        runTest(desc, () -> {
            try { Runtime.getRuntime().exec("calc"); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testProcessBuilder(String desc) {
        runTest(desc, () -> {
            try { new ProcessBuilder("calc").start(); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFileWrite(String desc) {
        runTest(desc, () -> {
            try { Files.write(Paths.get("C:\\Windows\\temp.txt"), "test".getBytes()); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testNetwork(String desc) {
        runTest(desc, () -> {
            try {
                Socket sock = new Socket();
                sock.connect(new InetSocketAddress("8.8.8.8", 53));
            } catch (Exception e) {
                throw new RuntimeException(e); }
        });
    }

    private static void testReflection(String desc) {
        runTest(desc, () -> {
            try {
                Method m = String.class.getMethod("toUpperCase");
                m.invoke("hello");
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testClassLoader(String desc) {
        runTest(desc, () -> {
            try { Class.forName("javax.crypto.Cipher"); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testSystemProperty(String desc) {
        runTest(desc, () -> {
            try {
                System.getProperty("user.home");
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    // --- 30 New Test Implementations ---

    private static void testFileCreateNewFile(String desc) {
        runTest(desc, () -> {
            try { new File("test.txt").createNewFile(); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFileMkdir(String desc) {
        runTest(desc, () -> {
            try { new File("test-dir").mkdir(); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFileDelete(String desc) {
        runTest(desc, () -> {
            try { new File("test.txt").delete(); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFileRenameTo(String desc) {
        runTest(desc, () -> {
            try { new File("test-dir").renameTo(new File("new-test-dir")); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFileInputStream(String desc) {
        runTest(desc, () -> {
            try { new FileInputStream("test.txt"); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFileOutputStream(String desc) {
        runTest(desc, () -> {
            try { new FileOutputStream("test.txt"); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFileWriter(String desc) {
        runTest(desc, () -> {
            try { new FileWriter("test.txt"); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFilesReadAllBytes(String desc) {
        runTest(desc, () -> {
            try { Files.readAllBytes(Paths.get("test.txt")); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFilesReadString(String desc) {
        runTest(desc, () -> {
            try { Files.readString(Paths.get("test.txt")); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFilesNewInputStream(String desc) {
        runTest(desc, () -> {
            try { Files.newInputStream(Paths.get("test.txt")); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFilesNewOutputStream(String desc) {
        runTest(desc, () -> {
            try { Files.newOutputStream(Paths.get("test.txt")); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFilesCreateFile(String desc) {
        runTest(desc, () -> {
            try { Files.createFile(Paths.get("test-nio.txt")); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFilesCreateDirectory(String desc) {
        runTest(desc, () -> {
            try { Files.createDirectory(Paths.get("test-nio-dir")); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFilesNioDelete(String desc) {
        runTest(desc, () -> {
            try { Files.delete(Paths.get("test-nio.txt")); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFilesMove(String desc) {
        runTest(desc, () -> {
            try { Files.move(Paths.get("test-nio-dir"), Paths.get("test-nio-dir-moved"), StandardCopyOption.REPLACE_EXISTING); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFilesCopy(String desc) {
        runTest(desc, () -> {
            try { Files.copy(Paths.get("test.txt"), Paths.get("test-copy.txt"), StandardCopyOption.REPLACE_EXISTING); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testClassGetConstructor(String desc) {
        runTest(desc, () -> {
            try { String.class.getConstructor(String.class); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testClassGetField(String desc) {
        runTest(desc, () -> {
            try { System.class.getField("out"); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testConstructorNewInstance(String desc) {
        runTest(desc, () -> {
            try {
                Constructor<String> c = String.class.getConstructor(String.class);
                c.newInstance("test");
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testReflectionSetAccessible(String desc) {
        runTest(desc, () -> {
            try {
                Field f = String.class.getDeclaredField("value");
                f.setAccessible(true);
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testUrlOpenConnection(String desc) {
        runTest(desc, () -> {
            try { new URL("http://www.google.com").openConnection(); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testUrlOpenStream(String desc) {
        runTest(desc, () -> {
            try { new URL("http://www.google.com").openStream(); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testServerSocketBind(String desc) {
        runTest(desc, () -> {
            try {
                ServerSocket s = new ServerSocket();
                s.bind(new InetSocketAddress(8080));
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testDatagramSocketSend(String desc) {
        runTest(desc, () -> {
            try {
                DatagramSocket s = new DatagramSocket();
                byte[] buf = "test".getBytes();
                DatagramPacket p = new DatagramPacket(buf, buf.length, InetAddress.getByName("8.8.8.8"), 53);
                s.send(p);
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testSystemExit(String desc) {
        runTest(desc, () -> {
            try { System.exit(1); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testSystemGetProperties(String desc) {
        runTest(desc, () -> {
            try { System.getProperties(); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testSystemGetEnv(String desc) {
        runTest(desc, () -> {
            try { System.getenv(); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testRuntimeLoad(String desc) {
        runTest(desc, () -> {
            try { Runtime.getRuntime().load("some.dll"); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testCryptoCipher(String desc) {
        runTest(desc, () -> {
            try { Cipher.getInstance("AES/CBC/PKCS5Padding"); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testCryptoMessageDigest(String desc) {
        runTest(desc, () -> {
            try { MessageDigest.getInstance("SHA-256"); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }
}
