package xyz.liujin.finalysis.extractor.tushare.util;

import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.lang.Nullable;
import xyz.liujin.finalysis.base.constant.StockConst;
import xyz.liujin.finalysis.base.constant.StockMarketEnum;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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

    public static void main(String[] args) {
        BigDecimal bigDecimal = parseBigDecimal("88.9978", 2);
        System.out.println();
    }
}
