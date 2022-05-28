package xyz.liujin.finalysis.base.executor;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * 项目通用线程池
 */
public class TaskPool implements Executor {
    private static final int corePoolSize;
    private static final int maxPoolSize;
    static {
        corePoolSize = Math.max(availableProcessors(), 100);
        maxPoolSize = Math.max(corePoolSize, 200);
    }

    private static class Singleton {
        private static final ExecutorService INSTANCE = ThreadPoolBuilder.builder()
                .poolName("finalysis-thread-pool")
                .corePoolSize(corePoolSize)
                .maximumPoolSize(maxPoolSize)
                .keepAliveTime(1)
                .timeUnit(TimeUnit.MINUTES)
                .workQueue(new ArrayBlockingQueue<>(5000))
                .build();
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
