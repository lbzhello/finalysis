package xyz.liujin.finalysis.base.executor;

import java.util.Objects;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 创建线程池，Builder 模式
 */
public class ThreadPoolBuilder {
    private int corePoolSize = Math.max(availableProcessors(), 100);
    private int maximumPoolSize = Math.max(corePoolSize, 200);
    private BlockingQueue<Runnable> workQueue = new ArrayBlockingQueue<>(5000);
    private long keepAliveTime = 1;
    private TimeUnit timeUnit = TimeUnit.MINUTES;
    private ThreadFactory threadFactory;
    private RejectedExecutionHandler rejectedHandler;

    private String poolName = "thread-pool";

    public static ThreadPoolBuilder builder() {
        return new ThreadPoolBuilder();
    }

    public ExecutorService build() {
        if (Objects.isNull(threadFactory)) {
            threadFactory = new ThreadFactory() {
                private final AtomicInteger threadNumber = new AtomicInteger(1);

                @Override
                public Thread newThread(Runnable r) {
                    Thread t = new Thread(r, poolName + "-" + threadNumber.getAndIncrement());

                    if (t.isDaemon()) {
                        t.setDaemon(false);
                    }

                    if (t.getPriority() != Thread.NORM_PRIORITY) {
                        t.setPriority(Thread.NORM_PRIORITY);
                    }
                    return t;
                }
            };
        }
        // 默认丢弃策略
        if (Objects.isNull(rejectedHandler)) {
            rejectedHandler = new ThreadPoolExecutor.AbortPolicy();
        }
        return new ThreadPoolExecutor(corePoolSize, maximumPoolSize, keepAliveTime, timeUnit, workQueue, threadFactory, rejectedHandler);
    }

    /**
     * 获取 CPU 核心数
     * @return
     */
    public static int availableProcessors() {
        return Runtime.getRuntime().availableProcessors();
    }

    public ThreadPoolBuilder corePoolSize(int corePoolSize) {
        this.corePoolSize = corePoolSize;
        return this;
    }

    public ThreadPoolBuilder maximumPoolSize(int maximumPoolSize) {
        this.maximumPoolSize = maximumPoolSize;
        return this;
    }

    public ThreadPoolBuilder workQueue(BlockingQueue<Runnable> workQueue) {
        this.workQueue = workQueue;
        return this;
    }

    public ThreadPoolBuilder keepAliveTime(long keepAliveTime) {
        this.keepAliveTime = keepAliveTime;
        return this;
    }

    public ThreadPoolBuilder timeUnit(TimeUnit timeUnit) {
        this.timeUnit = timeUnit;
        return this;
    }

    public ThreadPoolBuilder threadFactory(ThreadFactory threadFactory) {
        this.threadFactory = threadFactory;
        return this;
    }

    public ThreadPoolBuilder rejectedHandler(RejectedExecutionHandler rejectedHandler) {
        this.rejectedHandler = rejectedHandler;
        return this;
    }

    public ThreadPoolBuilder poolName(String poolName) {
        this.poolName = poolName;
        return this;
    }
}
