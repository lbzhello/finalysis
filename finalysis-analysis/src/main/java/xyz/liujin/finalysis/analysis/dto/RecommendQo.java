package xyz.liujin.finalysis.analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.base.page.PageQo;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 指标为 null 表示不使用此指标
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendQo {
    @Schema(description = "分析日期")
    private LocalDate date;
    @Schema(description = "量比放量指标")
    private HeavenVolRatioQo heavenVolRatio;
    @Schema(description = "日线突破指标，5 X 10")
    private FiveCrossTenQo fiveCrossTen;
    @Schema(description = "日线增势指标，5 > 10")
    private FiveAboveTenQo fiveAboveTen;
    @Schema(description = "成交额指标", example = "100000000")
    private BigDecimal minAmount;
    @Schema(description = "结果是否入库", example = "false")
    private boolean store;
    @Schema(description = "分页信息")
    private PageQo page;

}
