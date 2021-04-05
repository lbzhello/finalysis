package xyz.liujin.finalysis.daily.qo;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.base.page.Page;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KLineQo extends Page {
    @ApiModelProperty("股票代码；可空")
    private String code;

    @ApiModelProperty("股票代码列表；可空；code 存在时不生效")
    private List<String> codes;

    @ApiModelProperty("K 线日期；可空")
    private LocalDate date;

    @ApiModelProperty("开始日期；可空；date 存在时不生效")
    private LocalDate startDate;

    @ApiModelProperty("结束日期；可空；date 存在时不生效")
    private LocalDate endDate;
}
