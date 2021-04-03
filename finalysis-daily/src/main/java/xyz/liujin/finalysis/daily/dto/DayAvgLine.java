package xyz.liujin.finalysis.daily.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class DayAvgLine {
    // 股票代码 000001
    private String stockCode;
    // 当前日期 2021-01-13
    private LocalDate date;
    private BigDecimal current;
    // 5 日均线
    private BigDecimal avg5;
    // 10 日均线
    private BigDecimal avg10;
    // 20 日均线
    private BigDecimal avg20;
    // 30 日均线
    private BigDecimal avg30;

}
