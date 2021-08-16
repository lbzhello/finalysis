package xyz.liujin.finalysis.base.util;

import cn.hutool.core.text.CharSequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.time.ZoneOffset;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public final class DateUtils {
    private static final Logger logger = LoggerFactory.getLogger(DateUtils.class);

    public static final String DATE = "yyyy-MM-dd";
    public static final String ISO_DATE = "yyyy-MM-ddXXX";

    public static final String DATE_TIME = "yyyy-MM-dd";
    public static final String ISO_DATE_TIME = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX";

    /**
     * 返回 start - end 之前每天的日期
     * @param start
     * @param end
     * @return
     */
    public static Flux<LocalDate> iterateDays(LocalDate start, LocalDate end) {
        if (Objects.isNull(start) || Objects.isNull(end) || start.isAfter(end)) {
            logger.info("start or end date illegal, start {}, end {}", start, end);
            return Flux.empty();
        }

        List<LocalDate> items = new ArrayList<>();
        do {
            items.add(start);
            start = start.plusDays(1);
        } while (!start.isAfter(end));

        return Flux.fromIterable(items);
    }

    public static final OffsetDateTime now() {
        return OffsetDateTime.now();
    }

    /**
     * 获取年初日期
     * @return
     */
    public static final LocalDate beginOfYear() {
        return LocalDate.of(LocalDate.now().getYear(), 1, 1);
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

    public static final String format(LocalDate localDate, String pattern) {
        return DateTimeFormatter.ofPattern(pattern).format(localDate);
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
     * -> yyyy-MM-dd
     * @param localDate
     * @return
     */
    public static final String formatDate(LocalDate localDate) {
        return format(localDate, DATE);
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
     * 北京时间偏移
     */
    public static ZoneOffset ZONE_OFFSET_8 = ZoneOffset.ofHours(8);

    /**
     * yyyy-MM-dd -> OffsetDateTime
     *
     * @param text
     * @return
     */
    public static OffsetDateTime parseOffsetDate(String text) {
        return parseOffsetDate(text, DATE);
    }


    /**
     * 日期转 OffsetDateTime
     * @param text
     * @param pattern
     * @return
     */
    public static OffsetDateTime parseOffsetDate(String text, String pattern) {
        if (CharSequenceUtil.isBlank(text)) {
            return null;
        }
        return LocalDate.parse(text, DateTimeFormatter.ofPattern(pattern))
                .atTime(0, 0, 0).atOffset(ZONE_OFFSET_8);
    }

    /**
     * yyyy-MM-dd -> LocalDate
     * @param text
     * @return
     */
    public static LocalDate parseDate(String text) {
        return LocalDate.parse(text, DateTimeFormatter.ISO_DATE);
    }

    public static void main(String[] args) {
        System.out.println();
    }

    /**
     * str 转 LocalDate
     * @param text
     * @param pattern
     * @return
     */
    public static LocalDate parseDate(String text, String pattern) {
        if (CharSequenceUtil.isBlank(text)) {
            return null;
        }
        return LocalDate.parse(text, DateTimeFormatter.ofPattern(pattern));
    }

}
