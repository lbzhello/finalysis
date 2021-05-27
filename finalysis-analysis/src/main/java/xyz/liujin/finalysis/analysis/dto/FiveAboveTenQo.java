package xyz.liujin.finalysis.analysis.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@ApiModel("日线增势指标")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiveAboveTenQo {
    @ApiModelProperty(value = "最小持续天数，5 日线在 10 日线上方的最小天数", example = "3")
    private Integer days;

    @ApiModelProperty(value = "当前日期，默认数据库最新", hidden = true)
    private LocalDate date;

    @ApiModelProperty(value = "需要统计的股票列表", hidden = true)
    private List<String> codes;
}
