package xyz.liujin.finalysis.analysis.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import xyz.liujin.finalysis.base.page.PageReq;

import java.math.BigDecimal;
import java.time.LocalDate;

@ApiModel(description = "股票日数据请求对象")
@Data
public class DailyDateReq extends PageReq {
    @ApiModelProperty("当前日期")
    private LocalDate date;
    @ApiModelProperty("开始日期；当前日期设定后无效")
    private LocalDate startDate;
    @ApiModelProperty("结束日期；当前日期设定后无效")
    private LocalDate endDate;
    @ApiModelProperty("最小成交额")
    private BigDecimal minAmount;
}
