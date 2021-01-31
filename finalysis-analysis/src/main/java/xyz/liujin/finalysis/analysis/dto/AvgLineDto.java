package xyz.liujin.finalysis.analysis.dto;

import lombok.Data;

@Data
public class AvgLineDto {
    // 股票代码 000001
    private String stockCode;
    // 当前日期 2021-01-13
    private String date;
    private String current;
    // 5 日均线
    private String avg5;
    // 10 日均线
    private String avg10;
    // 20 日均线
    private String avg20;
    // 30 日均线
    private String avg30;

}
