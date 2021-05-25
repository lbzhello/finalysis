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
import java.util.List;

@ApiModel(description = "股票日数据请求对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyDataQo {
    @ApiModelProperty(value = "当前日期")
    private LocalDate date;
    @ApiModelProperty(value = "开始日期；当前日期设定后无效")
    private LocalDate startDate;
    @ApiModelProperty(value = "结束日期；当前日期设定后无效")
    private LocalDate endDate;
    @ApiModelProperty(value = "股票代码", hidden = true)
    private List<String> codes;
    @ApiModelProperty(value = "最小成交额", example = "1e9")
    private BigDecimal minAmount;
    @ApiModelProperty(value = "最小量比", example = "")
    private BigDecimal minVolRatio;
    @ApiModelProperty("分页信息")
    private PageQo page;
}
