package xyz.liujin.finalysis.analysis.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.lang.NonNull;
import xyz.liujin.finalysis.analysis.score.ScoreCustomizer;
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
 * 换手率比指标
 * 最近 recDays 天平均换手率，与过去 hisDays 天平均换手率的比值
 * 最近几天股票换手激增，表明股票开始受到关注
 * 如果换手率增大后，能够位置，则股票应该受到关注
 */
@ApiModel("增幅指标")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ScoreConfig(ScoreType.TURN_RATIO)
public class TurnRatioQo implements StrategyQo, ScoreCustomizer {
    @ApiModelProperty
    private LocalDate date;

    @ApiModelProperty(value = "需要统计的最近的天数", example = "3")
    @ScoreField("最近 %s 天")
    @NonNull
    private Integer recDays;

    @ApiModelProperty(value = "需要统计的过去的天数", example = "5")
    @ScoreField("过去 %s 天")
    @NonNull
    private Integer hisDays;

    @ApiModelProperty(value = "需要统计的过去的天数", example = "5")
    @ScoreField("换手率比值大于 %s")
    private BigDecimal minRatio;

    @ApiModelProperty(value = "最近最小平均成交额，一般应该大于 1 亿，过小的成交额没有太多意义", example = "1e9")
    @ScoreField("最近平均成交额大于 %s")
    private BigDecimal minAvgAmount;

    @ApiModelProperty(value = "需要统计的股票列表", hidden = true)
    private List<String> codes;

    @ApiModelProperty("分页信息")
    @ScorePage
    private PageQo page;

    @ApiModelProperty("得分")
    private int score;
}
