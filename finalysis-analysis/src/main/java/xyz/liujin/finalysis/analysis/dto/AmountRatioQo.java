package xyz.liujin.finalysis.analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import xyz.liujin.finalysis.analysis.score.ScoreType;
import xyz.liujin.finalysis.analysis.score.annotation.ScoreConfig;
import xyz.liujin.finalysis.analysis.score.annotation.ScoreField;
import xyz.liujin.finalysis.analysis.score.annotation.ScorePage;
import xyz.liujin.finalysis.analysis.strategy.StrategyQo;
import xyz.liujin.finalysis.base.page.PageQo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 选股策略-持续放量
 * 最近 recDays 天成交额与过去 historyDays 天成交额比值
 */
@Tag(name = "放量且持续的股票")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ScoreConfig(value = ScoreType.AMOUNT_RATIO)
public class AmountRatioQo implements StrategyQo {
    @Schema(description = "当前日期；")
    private LocalDate date;

    @Schema(description = "持续放量天数")
    @ScoreField("最近 %s 天")
    @NonNull
    private Integer recDays;

    @Schema(description = "需要比较的过去的天数")
    @ScoreField("过去 %s 天")
    @NonNull
    private Integer hisDays;

    @Schema(description = "最近成交额与历史成交额比值最小值")
    @ScoreField("成交额比值大于 s%")
    private BigDecimal minRatio;

    @Schema(description = "股票代码", hidden = true)
    private List<String> codes;

    @Schema(description = "分页信息")
    @ScorePage
    private PageQo page;
}
