package xyz.liujin.finalysis.analysis.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@ApiModel("量比放量指标")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeavenVolRatioQo {
    @ApiModelProperty(value = "统计天数，已当前天数为基准", example = "10")
    private Integer days;
    @ApiModelProperty(value = "days 天内存在最小量比", example = "1.5")
    private BigDecimal minVolRatio;
    @ApiModelProperty(value = "需要统计的股票列表", hidden = true)
    private List<String> codes;
    @ApiModelProperty(value = "开始日期，可选根据 days 计算", hidden = true)
    private LocalDate startDate;
    @ApiModelProperty(value = "结束日期，可选根据 days 计算", hidden = true)
    private LocalDate endDate;

    // 默认查询类, 10 天内存在量比大于等于 1.6
    public static HeavenVolRatioQo DEFAULT = HeavenVolRatioQo.builder()
            .days(10)
            .minVolRatio(BigDecimal.valueOf(1.5))
            .build();
}

