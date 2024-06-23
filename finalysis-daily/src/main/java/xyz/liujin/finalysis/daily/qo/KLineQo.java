package xyz.liujin.finalysis.daily.qo;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.*;
import xyz.liujin.finalysis.base.page.PageQo;

import java.time.LocalDate;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = false)
public class KLineQo {
    @Schema(description = "股票代码；可空", hidden = true)
    private String code;

    @Schema(description = "股票代码列表；可空；code 存在时不生效", hidden = true)
    private List<String> codes;

    @Schema(description = "K 线日期；可空")
    private LocalDate date;

    @Schema(description = "开始日期；可空；date 存在时不生效")
    private LocalDate startDate;

    @Schema(description = "结束日期；可空；date 存在时不生效")
    private LocalDate endDate;

    @Schema(description = "分页信息")
    private PageQo page;
}
