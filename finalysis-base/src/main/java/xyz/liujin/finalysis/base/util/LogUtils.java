package xyz.liujin.finalysis.base.util;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class LogUtils {
    public static void println(Object msg) {
        Thread currentThread = Thread.currentThread();
        String timeInfo = OffsetDateTime.now().format(DateTimeFormatter.ISO_OFFSET_DATE_TIME);
        String threadInfo = "[" + currentThread.getName() + ":" + currentThread.getId() + "]";
        System.out.println(timeInfo + " " + threadInfo + " " + msg);
    }
}
