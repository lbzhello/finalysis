package xyz.liujin.finalysis.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(autoResultMap = true) // 查询时 typeHandler 不生效问题
public class AvgLine {
    @TableId(type = IdType.AUTO )
    private Integer id;
    /**
     * 股票代码
     */
    private String stockCode;
    /**
     * 当前日期
     */
    private LocalDate date;
    /**
     * 当日价格（收盘价）
     */
    private BigDecimal current;
    /**
     * 5 日均线
     */
    private BigDecimal avg5;
    /**
     * 10 日均线
     */
    private BigDecimal avg10;
    /**
     * 20 日均线
     */
    private BigDecimal avg20;
    /**
     * 30 日均线
     */
    private BigDecimal avg30;
}
