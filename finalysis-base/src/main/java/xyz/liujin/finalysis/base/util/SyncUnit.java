package xyz.liujin.finalysis.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * 同步工具类，在一定时间内只允许一个线程执行
 */
public class SyncUnit {
    private static Logger logger = LoggerFactory.getLogger(SyncUnit.class);
    private Semaphore semaphore;

    private SyncUnit(Semaphore semaphore) {
        this.semaphore = semaphore;
    }

    /**
     * 创建一个同步器
     * @return
     */
    public static SyncUnit create() {
        return new SyncUnit(new Semaphore(1));
    }

    /**
     * 方法启动后，最少等待 {@param millis} 后才允许再次调用
     * 用于控制方法执行速率
     *
     * @param millis 在锁上最少需要等待的时间
     * @throws InterruptedException
     */
    public void waitMillis(long millis) {
        try {
            semaphore.acquire();
            // 再另一个线程中等待 millis 后释放锁
            Runnable runnable = () -> {
                try {
                    Thread.sleep(millis);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                } finally {
                    semaphore.release();
                }
            };
            new Thread(runnable).start();
        } catch (Exception e) {
            // 保证释放信号量
            semaphore.release();
            logger.error("thread sleep interrupted", e);
        }
    }
}
