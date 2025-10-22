package bachelor.agents;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class MyAgent {

    private static final String FILE_ADVICE_CLASS = "bachelor.test.MyFileAdvice";
    private static final String FILES_WRITE_ADVICE_CLASS = "bachelor.test.MyFilesWriteAdvice";
    private static final String NETWORK_ADVICE_CLASS = "bachelor.test.MyNetworkAdvice";
    private static final String OUTPUT_STREAM_ADVICE_CLASS = "bachelor.test.MyOutputStreamAdvice";
    private static final String WRITER_ADVICE_CLASS = "bachelor.test.MyWriterAdvice";
    private static final String INPUT_STREAM_ADVICE_CLASS = "bachelor.test.MyInputStreamAdvice";
    private static final String REFLECTION_ADVICE_CLASS = "bachelor.test.MyReflectionAdvice";
    private static final String METHOD_INVOKE_ADVICE_CLASS = "bachelor.test.MyMethodInvokeAdvice";
    private static final String CIPHER_ADVICE_CLASS = "bachelor.test.MyCipherAdvice";
    private static final String DIGEST_ADVICE_CLASS = "bachelor.test.MyDigestAdvice";
    private static final String EXEC_ADVICE_CLASS = "bachelor.test.MyExecAdvice";
    private static final String PROCESS_BUILDER_ADVICE_CLASS = "bachelor.test.MyProcessBuilderAdvice";
    private static final String SYSTEM_PROPERTY_ADVICE_CLASS = "bachelor.test.MySystemPropertyAdvice";
    private static String agentJarPath;
    private static List<String> classesToRetransform = Arrays.asList("java.io.File", "java.nio.file.Files", "java.util.Arrays", "java.util.List",
                                                                     "java.lang.reflect.Method", "java.io.OutputStream", "sun.nio.ch.ChannelOutputStream",
                                                                     "java.io.Writer", "java.net.InetSocketAddress",
                                                                     "javax.crypto.Cipher", "java.security.MessageDigest",
                                                                     "java.net.Socket", "java.lang.Runtime", "java.lang.ProcessBuilder", "java.lang.Class", "java.lang.System");



    public static void premain(String agentArguments, Instrumentation instrumentation) throws IOException {

        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        for (String arg : runtimeMxBean.getInputArguments()) {
            if (arg.startsWith("-javaagent:")) {
                String fullArgument = arg.substring(11);
                agentJarPath = fullArgument;
                break;
            }
        }

        new AgentBuilder.Default()
            .ignore(ElementMatchers.nameStartsWith("java.util.concurrent"))
            .with(RETRANSFORMATION)
            .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
//            .with(AgentBuilder.Listener.StreamWriting.toSystemError())
            .type(isSubTypeOf(java.io.File.class).and(ElementMatchers.not(isAbstract())))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                       .include(MyAgent.class.getClassLoader())
                       .advice(named("delete").and(ElementMatchers.takesArguments(0))
                                              .and(ElementMatchers.returns(boolean.class)), FILE_ADVICE_CLASS)
               )
            .type(named("java.nio.file.Files"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                               .include(MyAgent.class.getClassLoader())
                        .advice(named("write")
                                .and(ElementMatchers.takesArgument(0, java.nio.file.Path.class))
                                .and(ElementMatchers.takesArgument(1, byte[].class)),
                                FILES_WRITE_ADVICE_CLASS))
            .type(named("java.net.Socket"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(ElementMatchers.named("connect")
                                    .and(ElementMatchers.takesArgument(0, java.net.SocketAddress.class)),
                                    NETWORK_ADVICE_CLASS))
            .type(isSubTypeOf(java.io.OutputStream.class).and(ElementMatchers.not(isAbstract())))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("write")
                                    .and(ElementMatchers.takesArgument(0, byte[].class)),
                                    OUTPUT_STREAM_ADVICE_CLASS))
            .type(isSubTypeOf(java.io.Writer.class).and(ElementMatchers.not(isAbstract())))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("write")
                                    .and(ElementMatchers.takesArgument(0, char[].class)),
                                    WRITER_ADVICE_CLASS))
            .type(isSubTypeOf(java.io.InputStream.class).and(ElementMatchers.not(isAbstract())))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("read")
                                    .and(ElementMatchers.takesArgument(0, byte[].class)),
                                    INPUT_STREAM_ADVICE_CLASS))
            .type(named("java.lang.Class"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("getMethod")
                                    .or(named("getField"))
                                    .or(named("getConstructor"))
                                     .or(named("forName")),
                                    REFLECTION_ADVICE_CLASS))
            .type(named("java.lang.reflect.Method"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("invoke"), METHOD_INVOKE_ADVICE_CLASS))
            .type(named("javax.crypto.Cipher"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("getInstance"), CIPHER_ADVICE_CLASS))
            .type(named("java.security.MessageDigest"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("getInstance"), DIGEST_ADVICE_CLASS))
            .type(named("java.lang.Runtime"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("exec"), EXEC_ADVICE_CLASS))
            .type(named("java.lang.ProcessBuilder"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("start"), PROCESS_BUILDER_ADVICE_CLASS))
            .type(named("java.lang.System"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                    .include(MyAgent.class.getClassLoader())
                    .advice(named("getProperty"), SYSTEM_PROPERTY_ADVICE_CLASS))
            .installOn(instrumentation);

        try {
            for (String clazzString : classesToRetransform) {
                try {
                    Class<?> clazz = Class.forName(clazzString);
                    instrumentation.retransformClasses(clazz);
                } catch (ClassNotFoundException e) {
                    System.err.println("### Agent: Could not find class for retransformation: " + clazzString);
                }
            }
        } catch (Exception e) {
            System.err.println("### Agent: Error during retransformation request: " + e.getMessage());
            e.printStackTrace();
        }
    }
}