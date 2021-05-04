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

/**
 * 股票基本面指标
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(autoResultMap = true) // 查询时 typeHandler 不生效问题
public class DailyIndicator {
    @TableId(type = IdType.AUTO)
    private Long id;
    private String stockCode; // TS股票代码
    private LocalDate date; // 交易日期
    private BigDecimal close; // 当日收盘价
    private BigDecimal turnoverRate; // 换手率（%）
    private BigDecimal turnoverRateF; // 换手率（自由流通股）
    private BigDecimal volumeRatio; // 量比
    private BigDecimal pe; // 市盈率（总市值/净利润， 亏损的PE为空）
    private BigDecimal peTtm; // 市盈率（TTM，亏损的PE为空）
    private BigDecimal pb; // 市净率（总市值/净资产）
    private BigDecimal ps; // 市销率
    private BigDecimal psTtm; // 市销率（TTM）
    private BigDecimal dvRatio; // 股息率 （%）
    private BigDecimal dvTtm; // 股息率（TTM）（%）
    private BigDecimal totalShare; // 总股本 （股）
    private BigDecimal floatShare; // 流通股本 （股）
    private BigDecimal freeShare; // 自由流通股本 （股）
    private BigDecimal totalMv; // 总市值 （元）
    private BigDecimal circMv; // 流通市值（元）
}
