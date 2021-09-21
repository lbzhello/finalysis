package xyz.liujin.finalysis.analysis.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.base.page.PageQo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 放量持续指标查询类
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SustainHighAmountQo {
    // 股票代码
    private List<String> codes;
    // 最小比值，最近日期内成交额，与历史日期内成交额的比值
    private BigDecimal minRatio;
    // 最大比值，最近日期内成交额，与历史日期内成交额的比值
    private BigDecimal maxRatio;
    // 最近放量开始日期
    private LocalDate recentStartDate;
    // 最近放量结束日期
    private LocalDate recentEndDate;
    // 历史成交开始日期
    private LocalDate historyStartDate;
    // 历史成交结束日期
    private LocalDate historyEndDate;
    private PageQo page;
}
