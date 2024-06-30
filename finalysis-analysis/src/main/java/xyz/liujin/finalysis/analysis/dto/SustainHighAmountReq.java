package xyz.liujin.finalysis.analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.analysis.strategy.StrategyQo;
import xyz.liujin.finalysis.base.page.PageQo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 选股策略-持续放量
 * 最近 recentDays 天成交额与过去 historyDays 天成交额比值
 */
@Tag(name = "放量且持续的股票")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SustainHighAmountReq implements StrategyQo {
    @Schema(description = "当前日期；")
    private LocalDate date;
    @Schema(description = "股票代码", hidden = true)
    private List<String> codes;
    @Schema(description = "最近成交额与历史成交额比值最小值")
    @Builder.Default
    private BigDecimal minRatio = BigDecimal.valueOf(2);
    @Schema(description = "持续放量天数")
    @Builder.Default
    private Integer recentDays = 3;
    @Schema(description = "需要比较的过去的天数")
    @Builder.Default
    private Integer historyDays = 5;
    @Schema(description = "分页信息")
    private PageQo page;

    @Override
    public String getType() {
        return "deprecated";
    }
}
