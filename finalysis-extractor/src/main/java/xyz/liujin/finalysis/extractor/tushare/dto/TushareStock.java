package xyz.liujin.finalysis.extractor.tushare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TushareStock {
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
    private String board;
    /**
     * 股票状态。-1 退市；0 正常；1 暂停上市；2 ST；3 融资融券；
     */
    private String stat;

    private String listingDate;
}
