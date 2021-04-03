package xyz.liujin.finalysis.stock.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.base.constant.StockBoardEnum;

import java.time.LocalDate;

/**
 * 沪深股票信息
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(autoResultMap = true) // 查询时 typeHandler 不生效问题
public class Stock {
    /**
     * 股票代码
     */
    @TableId
    private String stockCode;
    /**
     * 股票名称
     */
    private String stockName;
    /**
     * 交易板块。0 未知；1 沪 A；2 深 A；3 创业板；4 科创板
     * @see StockBoardEnum
     */
    private Integer board;
    /**
     * 股票状态。-1 退市；0 正常；1 暂停上市；
     */
    private Integer stat;

    /**
     * 上市日期
     */
    private LocalDate listingDate;
}
