package xyz.liujin.finalysis.analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.base.page.PageQo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "股票日数据请求对象")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class DailyDataQo {
    @Schema(description = "当前日期")
    private LocalDate date;
    @Schema(description = "开始日期；当前日期设定后无效")
    private LocalDate startDate;
    @Schema(description = "结束日期；当前日期设定后无效")
    private LocalDate endDate;
    @Schema(description = "股票代码", hidden = true)
    private List<String> codes;
    @Schema(description = "最小成交额", example = "1e9")
    private BigDecimal minAmount;
    @Schema(description = "最小量比", example = "")
    private BigDecimal minVolRatio;
    @Schema(description = "分页信息")
    private PageQo page;
}
