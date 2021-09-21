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

/**
 * 选股策略-持续放量
 * 最近 recentDays 天成交额与过去 historyDays 天成交额比值
 */
@ApiModel(description = "放量且持续的股票")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SustainHighAmountReq {
    @ApiModelProperty(value = "当前日期；")
    private LocalDate date;
    @ApiModelProperty(value = "股票代码", hidden = true)
    private List<String> codes;
    @ApiModelProperty("最近成交额与历史成交额比值最小值")
    @Builder.Default
    private BigDecimal minRatio = BigDecimal.valueOf(2);
    @ApiModelProperty(value = "持续放量天数")
    @Builder.Default
    private Integer recentDays = 3;
    @ApiModelProperty("需要比较的过去的天数")
    @Builder.Default
    private Integer historyDays = 5;
    @ApiModelProperty("分页信息")
    private PageQo page;
}
