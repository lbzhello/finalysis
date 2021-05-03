package xyz.liujin.finalysis.extractor.tushare.dto;

import lombok.Builder;
import lombok.Data;

/**
 * Tushare 接口返回
 */
@Data
@Builder
public class TushareDailyIndicator {
    private String ts_code; // TS股票代码
    private String trade_date; // 交易日期
    private String close; // 当日收盘价
    private String turnover_rate; // 换手率（%）
    private String turnover_rate_f; // 换手率（自由流通股）
    private String volume_ratio; // 量比
    private String pe; // 市盈率（总市值/净利润， 亏损的PE为空）
    private String pe_ttm; // 市盈率（TTM，亏损的PE为空）
    private String pb; // 市净率（总市值/净资产）
    private String ps; // 市销率
    private String ps_ttm; // 市销率（TTM）
    private String dv_ratio; // 股息率 （%）
    private String dv_ttm; // 股息率（TTM）（%）
    private String total_share; // 总股本 （万股）
    private String float_share; // 流通股本 （万股）
    private String free_share; // 自由流通股本 （万）
    private String total_mv; // 总市值 （万元）
    private String circ_mv; // 流通市值（万元）
}
