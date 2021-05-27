package xyz.liujin.finalysis.analysis.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@ApiModel("量比放量指标")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeavenVolRatioQo {
    @ApiModelProperty(value = "统计天数", example = "10")
    private Integer days;
    @ApiModelProperty(value = "days 天内存在最小量比", example = "1.5")
    private BigDecimal minVolRatio;
    @ApiModelProperty(value = "需要统计的股票列表", hidden = true)
    private List<String> codes;
}

