package xyz.liujin.finalysis.analysis.dto;

import lombok.Data;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
public class AvgLineDto {
    // 股票代码
    private String stockCode;
    // 当前日期
    private LocalDate date;
    // 5 日均线
    private BigDecimal avg5;
    // 10 日均线
    private BigDecimal avg10;
    // 20 日均线
    private BigDecimal avg20;
    // 30 日均线
    private BigDecimal avg30;

}
