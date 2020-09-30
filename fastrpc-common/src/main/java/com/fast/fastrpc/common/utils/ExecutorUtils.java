package com.fast.fastrpc.common.utils;

import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * @author yiji
 * @version : ExecutorUtil.java, v 0.1 2020-09-30
 */
public class ExecutorUtils {

    public static void shutdownAndAwaitTermination(ExecutorService pool) {
        shutdownAndAwaitTermination(pool, -1);
    }

    /**
     * Close the thread pool.
     * https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/ExecutorService.html
     *
     * @param pool to be shutdown.
     */
    public static void shutdownAndAwaitTermination(ExecutorService pool, int timeout) {
        // Terminated or shutdown already.
        if (isTerminated(pool)) return;

        // Disable new tasks from being submitted
        pool.shutdown();
        try {

            if (timeout <= 0) {
                // Cancel currently executing tasks immediately.
                pool.shutdownNow();
            }

            // Wait a while for existing tasks to terminate
            if (!pool.awaitTermination(timeout, TimeUnit.MILLISECONDS)) {
                // Cancel currently executing tasks
                pool.shutdownNow();
            }
        } catch (InterruptedException e) {
            // (Re-)Cancel if current thread also interrupted
            pool.shutdownNow();
            // Preserve interrupt status
            Thread.currentThread().interrupt();
        }
    }

    public static boolean isTerminated(Executor executor) {
        if (executor instanceof ExecutorService) {
            ExecutorService executorService = (ExecutorService) executor;
            if (executorService.isTerminated()
                    || executorService.isShutdown()) {
                return true;
            }
        }
        return false;
    }
}
