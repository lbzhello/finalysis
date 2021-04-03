package xyz.liujin.finalysis.base.executor;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.*;

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
                new ArrayBlockingQueue<>(1),
                ThreadFactoryBuilder.create()
                        .setNamePrefix("finalysis-pool-")
                        .build()
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
