package xyz.liujin.finalysis.common.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.Semaphore;

/**
 * 同步工具类
 */
public class SyncUtils {
    private static Logger logger = LoggerFactory.getLogger(SyncUtils.class);
    private static Semaphore semaphore = new Semaphore(1);

    /**
     * 方法启动后，最少等待 {@param millis} 后才允许其他方法执行
     * 用于控制方法执行速率
     *
     * @param millis 在锁上最少需要等待的时间
     * @throws InterruptedException
     */
    public static void waitMillis(long millis) {
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
        } catch (InterruptedException e) {
            logger.error("thread sleep interrupted", e);
        }
    }
}
