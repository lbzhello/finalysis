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
     * 股票代码; 格式如下
     * 000001.SZ
     * 600001.SH
     */
    private String stockCode;
    /**
     * 股票名称
     */
    private String stockName;
    /**
     * 股票状态; L 上市， D 退市， P 暂停，
     */
    private String stat;
    /**
     * 上市日期；yyyyMMdd
     */
    private String listingDate;
}
