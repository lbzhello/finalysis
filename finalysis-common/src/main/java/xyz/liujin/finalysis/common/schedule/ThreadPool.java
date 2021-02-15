package xyz.liujin.finalysis.common.schedule;

import cn.hutool.core.thread.ThreadFactoryBuilder;

import java.util.concurrent.*;

public class ThreadPool implements Executor {
    private static final int corePoolSize;
    private static final int maxPoolSize = 100;
    static {
        corePoolSize = Math.max(Runtime.getRuntime().availableProcessors(), 10);
    }

    private static class Singleton {
          private static final ExecutorService INSTANCE = new ThreadPoolExecutor(
                  corePoolSize,
                  maxPoolSize,
                  1, TimeUnit.MINUTES,
                  new ArrayBlockingQueue<>(0),
                  ThreadFactoryBuilder.create()
                          .setNamePrefix("finalysis-thread-pool")
                          .build()
        );
    }

    public static final ExecutorService getInstance() {
        return Singleton.INSTANCE;
    }

    @Override
    public void execute(Runnable command) {
        getInstance().execute(command);
    }
}
