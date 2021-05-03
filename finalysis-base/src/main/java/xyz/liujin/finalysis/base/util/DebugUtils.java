package xyz.liujin.finalysis.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * 调试工具类
 */
public class DebugUtils {
    private static final Logger logger = LoggerFactory.getLogger(DebugUtils.class);

    /**
     * 线程等待一段时间再退出
     * @param millis
     */
    public static final void waitMillis(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            logger.error("failed to wait millis", e);
        }
    }
}
