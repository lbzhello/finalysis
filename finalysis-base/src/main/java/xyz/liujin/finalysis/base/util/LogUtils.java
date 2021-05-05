package xyz.liujin.finalysis.base.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class LogUtils {
    private static final Logger logger = LoggerFactory.getLogger(LogUtils.class);

    public static void println(Object msg) {
        Thread currentThread = Thread.currentThread();
        String timeInfo = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String threadInfo = "[" + currentThread.getName() + ":" + currentThread.getId() + "]";
        logger.debug(timeInfo + " " + threadInfo + " " + msg);
    }
}
