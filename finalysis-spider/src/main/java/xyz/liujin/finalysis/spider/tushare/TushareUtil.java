package xyz.liujin.finalysis.spider.tushare;

import cn.hutool.core.text.CharSequenceUtil;
import xyz.liujin.finalysis.spider.constant.StockConst;

public class TushareUtil {
    /**
     * 去除股票代码后缀 000001.SZ -> 000001
     * @return
     */
    public static final String removeSuffix(String stockCode) {
        return CharSequenceUtil.removeAny(stockCode, StockConst.SH, StockConst.SZ, StockConst.POINT);
    }
}
