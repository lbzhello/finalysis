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
    @ApiModelProperty("当前日期")
    private LocalDate date;
    @ApiModelProperty("开始日期；当前日期设定后无效")
    private LocalDate startDate;
    @ApiModelProperty("结束日期；当前日期设定后无效")
    private LocalDate endDate;
    @ApiModelProperty("股票代码")
    private List<String> codes;
    @ApiModelProperty("最小成交额")
    private BigDecimal minAmount;
    @ApiModelProperty("分页信息")
    private PageQo page;
}
