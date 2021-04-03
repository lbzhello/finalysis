package xyz.liujin.finalysis.extractor.tushare.util;

import cn.hutool.core.text.CharSequenceUtil;
import xyz.liujin.finalysis.base.constant.StockConst;
import xyz.liujin.finalysis.base.constant.StockMarketEnum;

public class TushareUtil {
    /**
     * 去除股票代码后缀 000001.SZ -> 000001
     * @return
     */
    public static String removeSuffix(String stockCode) {
        return CharSequenceUtil.removeAny(stockCode, StockConst.SH, StockConst.SZ, StockConst.POINT);
    }

    /**
     * 添加交易市场后缀 000001 -> 000001.SZ
     * @param stockCode
     * @return
     */
    public static String appendSuffix(String stockCode) {
        return CharSequenceUtil.isBlank(stockCode) ? "" : stockCode + "." + StockMarketEnum.getMarket(stockCode);
    }
}
