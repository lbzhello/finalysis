package xyz.liujin.finalysis.analysis.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.analysis.score.ScoreType;
import xyz.liujin.finalysis.analysis.score.annotation.ScoreConfig;
import xyz.liujin.finalysis.analysis.score.annotation.ScoreField;
import xyz.liujin.finalysis.analysis.score.annotation.ScorePage;
import xyz.liujin.finalysis.analysis.strategy.StrategyQo;
import xyz.liujin.finalysis.base.page.PageQo;

import java.time.LocalDate;
import java.util.List;

/**
 * 最低价格支撑指标
 * 当前股价没有跌破最近最低价格，说明股票开始企稳，若在突破初期，往往意味着洗盘结束
 */
@ApiModel("最低价支撑")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ScoreConfig(value = ScoreType.MINIMUM_PRICE_SUPPORT, score = 5)
public class MinimumPriceSupportQo implements StrategyQo {
    @ApiModelProperty
    private LocalDate date;

    @ApiModelProperty(value = "最近的天数，当前股价没有跌破最近最低价", example = "3")
    @ScoreField("最近 %s 天没有新低")
    private Integer recDays;

    @ApiModelProperty(value = "需要统计的股票列表", hidden = true)
    private List<String> codes;

    @ApiModelProperty("分页信息")
    @ScorePage
    private PageQo page;

}
