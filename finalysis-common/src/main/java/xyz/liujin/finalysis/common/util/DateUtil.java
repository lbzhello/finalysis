package xyz.liujin.finalysis.common.util;

import cn.hutool.core.text.CharSequenceUtil;

import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public final class DateUtil {
    public static final String DATE = "yyyy-MM-dd";
    public static final String ISO_DATE = "yyyy-MM-ddXXX";

    public static final String DATE_TIME = "yyyy-MM-dd";
    public static final String ISO_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    public static final OffsetDateTime now() {
        return OffsetDateTime.now();
    }

    /**
     * 根据给定的 pattern 格式化时间
     * @param offsetDateTime
     * @param pattern
     * @return
     */
    public static final String format(OffsetDateTime offsetDateTime, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(offsetDateTime);
    }

    /**
     * -> yyyy-MM-dd
     * @param offsetDateTime
     * @return
     */
    public static final String formatDate(OffsetDateTime offsetDateTime) {
        return format(offsetDateTime, DATE);
    }

    /**
     * -> yyyy-MM-dd+01:00
     * @param offsetDateTime
     * @return
     */
    public static final String formatISODate(OffsetDateTime offsetDateTime) {
        return format(offsetDateTime, ISO_DATE);
    }

    /**
     * -> yyyy-MM-dd HH:mm:ss
     * @param offsetDateTime
     * @return
     */
    public static final String formatDateTime(OffsetDateTime offsetDateTime) {
        return format(offsetDateTime, DATE_TIME);
    }

    /**
     * -> yyyy-MM-dd'T'HH:mm:ss.SSSXXX
     * @param offsetDateTime
     * @return
     */
    public static final String formatISODateTime(OffsetDateTime offsetDateTime) {
        return format(offsetDateTime, ISO_DATE_TIME);
    }

    /************************* str -> OffsetDateTime *****************************/


    /**
     * 用于拼接日期
     */
    public static final String ISO_UTC_PLUS_8_SUFFIX = "T00:00:00+08:00";

    /**
     * yyyy-MM-dd -> OffsetDateTime
     *
     * @param text
     * @return
     */
    public static OffsetDateTime parseDate(String text) {
        if (CharSequenceUtil.isBlank(text)) {
            return null;
        }
        return OffsetDateTime.parse(text + ISO_UTC_PLUS_8_SUFFIX);
    }

}
