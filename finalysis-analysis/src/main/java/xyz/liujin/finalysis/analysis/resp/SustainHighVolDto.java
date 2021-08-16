package xyz.liujin.finalysis.analysis.resp;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

/**
 * 持续放量响应类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SustainHighVolDto {
    private String stockCode;
    // 最近成交额
    private BigDecimal recentAmount;
    // 过去成交额
    private BigDecimal historyAmount;
    // 最近成交额与过去成交额比值
    private BigDecimal ratio;
}
