package xyz.liujin.finalysis.daily.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

/**
 * 股票均线数据视图 view
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(autoResultMap = true) // 查询时 typeHandler 不生效问题
public class VAvgLine {
    // 股票代码 000001
    private String stockCode;
    // 当前日期 2021-01-13
    private LocalDate date;
    // 当前价格
    private BigDecimal current;
    // 5 日均线
    private BigDecimal avg5;
    // 10 日均线
    private BigDecimal avg10;
    // 20 日均线
    private BigDecimal avg20;
    // 30 日均线
    private BigDecimal avg30;

}
