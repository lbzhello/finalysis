package xyz.liujin.finalysis.analysis.dto;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.base.page.PageQo;

import java.time.LocalDate;

/**
 * 指标为 null 表示不使用此指标
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class RecommendQo {
    @ApiModelProperty("分析日期")
    private LocalDate date;
    @ApiModelProperty("量比放量指标")
    private HeavenVolRatioQo heavenVolRatio;
    @ApiModelProperty("日线突破指标，5 X 10")
    private FiveCrossTenQo fiveCrossTen;
    @ApiModelProperty("日线增势指标，5 > 10")
    private FiveAboveTenQo fiveAboveTen;
    @ApiModelProperty("分页信息")
    private PageQo page;

}
