package xyz.liujin.finalysis.daily.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 股票日数据
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyData {
    // 股票代码
    private String stockCode;
    // 股票名称
    private String stockName;
    // 日期
    private LocalDate date;

    // 日 K 数据
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    private BigDecimal low;

    // 涨跌
    private BigDecimal change;
    private BigDecimal pctChange;

    // 成交量
    private Long volume;
    // 量比
    private BigDecimal volumeRatio;
    // 成交额
    private BigDecimal amount;
    // 换手
    private BigDecimal turn;
    // 总市值
    private BigDecimal marketValue;
}
