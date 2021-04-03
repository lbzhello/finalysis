package xyz.liujin.finalysis.daily.entity;

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
     * 统计天数
     */
    private Integer statistic;

    /**
     * 均线，days 天均值
     */
    private BigDecimal avg;
}
