package pl.setblack.wth.forkjoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import pl.setblack.wth.illegals.j21.FinalFieldUtil;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ForkJoinPool;

public final class Investigate {

    private Investigate() {
    }

    private static final String JVM_OPTIONS_TO_ADD = "--add-opens java.base/jdk.internal.misc=ALL-UNNAMED --add-opens java.base/jdk.internal.reflect=ALL-UNNAMED";

    public static void detectCommonForkJoinPoolUse(Runnable action, Option... options) {
        try {
            var spyExecutor = new SpyExecutor(action);
            FinalFieldUtil.setStaticFinalField(ForkJoinPool.class.getDeclaredField("common"), spyExecutor);
        } catch (Throwable t) {

            if (Arrays.stream(options).anyMatch(option -> option == Option.WarnToSlf4j)) {
                final Logger logger = LoggerFactory.getLogger(Investigate.class);
                logger.warn("Unable to install common pool in fork join detection, maybe you need to add JVM options: " + JVM_OPTIONS_TO_ADD, t);
            } else {
                System.out.println("Unable to install common pool in fork join detection, maybe you need to add JVM options: " + JVM_OPTIONS_TO_ADD);
                t.printStackTrace();
            }
            if (Arrays.stream(options).allMatch(option -> option != Option.DontFailOnInstall)) {
                throw new RuntimeException("Unable to install coomon pool in fork join detection", t);
            }
        }
    }

    public static final class Actions {
        public static Runnable failWhenPackagesInStacktrace(List<String> disallowedPackages) {
            return () -> {
                StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
                for (StackTraceElement stackTraceElement : stackTrace) {
                    for (String disallowedPackage : disallowedPackages) {
                        if (stackTraceElement.getClassName().startsWith(disallowedPackage)) {
                            throw new RuntimeException("Illegal use of common ForkJoinPool from: " + disallowedPackage + " detected in [" + Thread.currentThread().getName() + "]");
                        }
                    }
                }
            };
        }
    }
}


