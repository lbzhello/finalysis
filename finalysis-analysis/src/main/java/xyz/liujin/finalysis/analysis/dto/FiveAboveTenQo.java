package xyz.liujin.finalysis.analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.analysis.score.ScoreType;
import xyz.liujin.finalysis.analysis.score.annotation.ScoreConfig;
import xyz.liujin.finalysis.analysis.strategy.StrategyQo;
import xyz.liujin.finalysis.base.page.PageQo;

import java.time.LocalDate;
import java.util.List;

@Tag(name = "日线增势指标")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ScoreConfig(ScoreType.FIVE_ABOVE_TEN)
public class FiveAboveTenQo implements StrategyQo {
    @Schema(description = "最小持续天数，5 日线在 10 日线上方的最小天数", example = "3")
    private Integer days;

    @Schema(description = "当前日期，默认数据库最新", hidden = true)
    private LocalDate date;

    @Schema(description = "需要统计的股票列表", hidden = true)
    private List<String> codes;

    public static FiveAboveTenQo DEFAULT = FiveAboveTenQo.builder()
            .days(3)
            .build();

    @Schema(description = "分页信息")
    private PageQo page;

    @Override
    public String getType() {
        return ScoreType.FIVE_ABOVE_TEN;
    }
}
