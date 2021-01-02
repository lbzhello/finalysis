package xyz.liujin.finalysis.spider.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class StockDto {
    /**
     * 股票代码
     */
    private String stockCode;
    /**
     * 股票名称
     */
    private String stockName;
    /**
     * 交易板块。0 未知；1 沪 A；2 深 A；3 创业板；4 科创板
     */
    private Integer board;
    /**
     * 股票状态。-1 退市；0 正常；1 融资融券；2 ST
     */
    private Integer stat;
}
