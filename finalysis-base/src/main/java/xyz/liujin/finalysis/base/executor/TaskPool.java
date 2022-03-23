package xyz.liujin.finalysis.base.executor;

import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 项目通用线程池
 */
public class TaskPool implements Executor {
    private static final int corePoolSize;
    private static final int maxPoolSize = 200;
    static {
        corePoolSize = Math.max(availableProcessors(), 100);
    }

    private static class Singleton {
        private static ExecutorService INSTANCE = new ThreadPoolExecutor(
                corePoolSize,
                maxPoolSize,
                1, TimeUnit.MINUTES,
                new ArrayBlockingQueue<>(5000),
                new ThreadFactory() {
                    private final AtomicInteger threadNumber = new AtomicInteger(1);
                    @Override
                    public Thread newThread(Runnable r) {
                        Thread t = new Thread(r, "finalysis-thread-pool-" + threadNumber.getAndIncrement());

                        if (t.isDaemon()) {
                            t.setDaemon(false);
                        }

                        if (t.getPriority() != Thread.NORM_PRIORITY) {
                            t.setPriority(Thread.NORM_PRIORITY);
                        }
                        return t;
                    }
                }
        );
    }

    public static ExecutorService getInstance() {
        return Singleton.INSTANCE;
    }

    /**
     * 获取 CPU 核心数
     * @return
     */
    public static int availableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    @Override
    public void execute(Runnable command) {
        getInstance().execute(command);
    }
}
