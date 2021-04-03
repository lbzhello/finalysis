package xyz.liujin.finalysis.base.constant;

import cn.hutool.core.text.CharSequenceUtil;

public enum StockMarketEnum {
    SH("SH"), // 伤害证券交易所
    SZ("SZ"), // 深圳证券交易所
    UNKNOWN("UNKNOWN"),
    ;
    private String name;

    StockMarketEnum(String name) {
        this.name = name;
    }

    /**
     * 根据交易代码，获取交易市场简称
     * @param stockCode
     * @return
     */
    public static String getMarket(String stockCode) {
        if (CharSequenceUtil.isBlank(stockCode)) {
            return UNKNOWN.getName();
        }
        return stockCode.startsWith("6") || stockCode.startsWith("9") ? SH.getName() : SZ.getName();
    }

    public String getName() {
        return name;
    }
}
