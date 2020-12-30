package xyz.liujin.finalysis.spider.constant;

/**
 * 深圳证券交易所接口数据
 */
public final class SzsePaths {
    public static final String POST_SEC_CHECK = "http://www.szse.cn/api/search/secCheck?random=0.5875444874366631";

    /**
     * 当日分时
     */
    public static final String GET_TIME_DATA = "http://www.szse.cn/api/market/ssjjhq/getTimeData?random=0.4161868433218474&marketId=1&code=300124";

    /**
     * 日 k
     */
    public static final String GET_HISTORY_DATA_OF_DAY =  "http://www.szse.cn/api/market/ssjjhq/getHistoryData?random=0.9198023733231868&cycleType=32&marketId=1&code=300124";

    /**
     * 周 K
     */
    public static final String GET_HISTORY_DATA_OF_WEEK =  "http://www.szse.cn/api/market/ssjjhq/getHistoryData?random=0.9653984934240163&cycleType=33&marketId=1&code=300124";

    /**
     * 月 K
     */
    public static final String GET_HISTORY_DATA_OF_MONTH =  "http://www.szse.cn/api/market/ssjjhq/getHistoryData?random=0.2642655339486879&cycleType=34&marketId=1&code=300124";

}
