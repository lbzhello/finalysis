package xyz.liujin.finalysis.spider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.time.OffsetDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KLineDto {
    private Integer stockCode;
    private OffsetDateTime startTime;
    private OffsetDateTime endTime;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    private BigDecimal low;
    private Integer volume;
    private BigDecimal turnover;
    private BigDecimal volumeRatio;
    private BigDecimal turnoverRate;
    private BigDecimal committee;
    private BigDecimal selling;
    private BigDecimal buying;
}
