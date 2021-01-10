package xyz.liujin.finalysis.common.constant;

import cn.hutool.core.text.CharSequenceUtil;

public enum  MarketEnum {
    SH("SH"), // 伤害证券交易所
    SZ("SZ"), // 深圳证券交易所
    UNKNOWN("UNKNOWN"),
    ;
    private String name;

    MarketEnum(String name) {
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
