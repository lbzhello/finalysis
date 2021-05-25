package xyz.liujin.finalysis.analysis.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
    @ApiModelProperty("分析日期")
    private LocalDate date;
    @ApiModelProperty("量比放量指标")
    private HeavenVolRatio heavenVolRatio;
    @ApiModelProperty("日线突破指标，5 X 10")
    private FiveCrossTen fiveCrossTen;
    @ApiModelProperty("日线增势指标，5 > 10")
    private FiveAboveTen fiveAboveTen;
    @ApiModelProperty("分页信息")
    private PageQo page;

    @ApiModel("量比放量指标")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class HeavenVolRatio {
        @ApiModelProperty(value = "统计天数", example = "10")
        private Integer days;
        @ApiModelProperty(value = "days 天内存在最小量比", example = "1.5")
        private BigDecimal minVolRatio;
    }

    @ApiModel("日线突破指标")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class FiveCrossTen {
        @ApiModelProperty(value = "最大突破天数，5 日线超过 10 日线的最大天数", example = "3")
        private Integer days;
    }

    @ApiModel("日线增势指标")
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class FiveAboveTen {
        @ApiModelProperty(value = "最小持续天数，5 日线在 10 日线上方的最小天数", example = "3")
        private Integer days;
    }


}
