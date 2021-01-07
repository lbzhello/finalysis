package xyz.liujin.finalysis.spider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KLineDto {
    private Integer id;
    private String stockCode;
    private String dateTime;
    private String open;
    private String close;
    private String high;
    private String low;
    private String change;
    private String pctChange;
    private String volume;
    private String amount;
    private String volumeRatio;
    private String turn;
    private String committee;
    private String selling;
    private String buying;
}
