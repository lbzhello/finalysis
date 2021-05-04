package xyz.liujin.finalysis.extractor.tushare.util;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;
import xyz.liujin.finalysis.base.constant.StockConst;
import xyz.liujin.finalysis.base.constant.StockMarketEnum;
import xyz.liujin.finalysis.base.util.DateUtils;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.List;
import java.util.Optional;

public class TushareUtil {
    public static final String YYYYMMDD = "yyyyMMdd";

    /**
     * tushare 格式股票代码转换
     * 去除股票代码后缀 000001.SZ -> 000001
     * @return
     */
    public static String parseCode(@Nullable String stockCode) {
        return CharSequenceUtil.removeAny(stockCode, StockConst.SH, StockConst.SZ, StockConst.POINT);
    }

    /**
     * 股票代码转 tushare 格式
     * 添加交易市场后缀 000001 -> 000001.SZ
     * @param stockCode
     * @return
     */
    public static String formatCode(@Nullable String stockCode) {
        return CharSequenceUtil.isBlank(stockCode) ? "" : stockCode + "." + StockMarketEnum.getMarket(stockCode);
    }

    /**
     * tushare 日期解析
     * yyyyMMdd -> LocalDate
     * @param dateStr
     * @return
     */
    public static @Nullable LocalDate parseDate(@Nullable String dateStr) {
        return Optional.ofNullable(dateStr)
                .map(it -> LocalDate.parse(it, DateTimeFormatter.ofPattern(YYYYMMDD)))
                .orElse(null);
    }

    /**
     * 日期转 tushare 格式
     * @param localDate
     * @return
     */
    public static String formatDate(@Nullable LocalDate localDate) {
        return Optional.ofNullable(localDate)
                .map(it -> it.format(DateTimeFormatter.ofPattern(YYYYMMDD)))
                .orElse("");
    }

    /**
     * str 转 bigDecimal
     * @param moneyStr
     * @param scale 放大比例
     * @return
     */
    public static BigDecimal parseBigDecimal(@Nullable String moneyStr, int scale) {
        return Optional.ofNullable(moneyStr)
                .map(it -> new BigDecimal(moneyStr).multiply(BigDecimal.valueOf(scale)))
                .orElse(BigDecimal.ZERO);
    }

    /**
     * str 转 BigDecimal
     * @return
     */
    public static BigDecimal parseBigDecimal(@Nullable String moneyStr) {
        return parseBigDecimal(moneyStr, 1);
    }

    /**
     * tushare 最多返回 5000 条记录, 为了不使请求数据丢失，
     * 这里将 codes 分割，使其在 规定的日期内最多返回 5000 条数据
     * @return startDate, endDate, codes
     */
    // 返回最大记录数，日线每日 1 条，最多 5000 条
    private static final long MAX_ITEMS = 5000L;
    // 每次请求 codes 最大值
    private static final int MAX_CODES = 100;
    public static Flux<Tuple3<String, String, List<String>>> splitCodes(String startDate, String endDate, List<String> codes) {
        if (ArrayUtil.isEmpty(codes)) {
            return Flux.just(Tuples.of(startDate, endDate, List.of()));
        }

        return Flux.create(sink -> {
            LocalDate start = DateUtils.parseDate(startDate);
            LocalDate end = DateUtils.parseDate(endDate);

            // 最多间隔 5000 天（每天一条数据）
            long diff;
            while ((diff = start.until(end, ChronoUnit.DAYS) + 1) >= MAX_ITEMS) {
                // 没个 code 生成 MAX_ITEMS 条数据
                String from = DateUtils.formatDate(start);
                String to = DateUtils.formatDate(start.plusDays(MAX_ITEMS - 1));
                Flux.fromIterable(codes)
                        .subscribe(code -> {
                            sink.next(Tuples.of(from, to, List.of(code)));
                        });

                // 继续校验是否超过
                start = start.plusDays(MAX_ITEMS);
            }

            // 获取日期区间
            String startStr = DateUtils.formatDate(start);
            String endStr = DateUtils.formatDate(end);

            // 计算每次循环的 codes 数
            // codes
            int div = Math.min(Math.toIntExact(MAX_ITEMS / diff), MAX_CODES);
            int len = codes.size();

            int from = 0;
            int to = div;
            while (to <= len) {

                List<String> range = codes.subList(from, to);
                sink.next(Tuples.of(startStr, endStr, range));
                from = to;
                to = to + div;
            }

            // 剩余的 codes
            if (from < len) {
                List<String> range = codes.subList(from, len);
                sink.next(Tuples.of(startStr, endStr, range));
            }

            sink.complete();
        });

    }

    public static void main(String[] args) throws Exception {
        splitCodes("2021-01-26", "2021-01-26", List.of("a", "b", "c", "d", "e"))
                .subscribe(it -> {
                    System.out.println(it.getT1() + " " + it.getT2() + " " + it.getT3());
                });
    }
}
