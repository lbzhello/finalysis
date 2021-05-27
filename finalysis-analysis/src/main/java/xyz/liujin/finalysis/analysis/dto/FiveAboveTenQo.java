package xyz.liujin.finalysis.analysis.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@ApiModel("日线增势指标")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiveAboveTenQo {
    @ApiModelProperty(value = "最小持续天数，5 日线在 10 日线上方的最小天数", example = "3")
    private Integer days;
}
