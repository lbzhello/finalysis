package xyz.liujin.finalysis.spider.constant;

/**
 * 深圳证券交易所接口数据
 */
public final class SzseConst {
    /**
     *  http json 响应数据字段
     */
    public static final String DATA = "data";
    /**
     * 响应元数据，分页信息等
     */
    public static final String METADATA = "metadata";

    public static final String POST_SEC_CHECK = StockSiteConst.SZSE + "/api/search/secCheck?random=0.5875444874366631";

    /**
     * 当日分时
     */
    public static final String GET_TIME_DATA = StockSiteConst.SZSE + "/api/market/ssjjhq/getTimeData?random=0.4161868433218474&marketId=1&code=300124";

    /**
     * 日 k
     */
    public static final String GET_HISTORY_DATA_OF_DAY =  StockSiteConst.SZSE + "/api/market/ssjjhq/getHistoryData?random=0.9198023733231868&cycleType=32&marketId=1&code=300124";

    /**
     * 周 K
     */
    public static final String GET_HISTORY_DATA_OF_WEEK =  StockSiteConst.SZSE + "/api/market/ssjjhq/getHistoryData?random=0.9653984934240163&cycleType=33&marketId=1&code=300124";

    /**
     * 月 K
     */
    public static final String GET_HISTORY_DATA_OF_MONTH =  StockSiteConst.SZSE + "/api/market/ssjjhq/getHistoryData?random=0.2642655339486879&cycleType=34&marketId=1&code=300124";

    /**
     * 获取深市所有股票指定日期行情
     */
    public static final String GET_REPORT = StockSiteConst.SZSE + "/api/report/ShowReport/data" +
            "?SHOWTYPE=JSON" +
            "&CATALOGID=1815_stock" +
            "&TABKEY=tab1" +
            "&txtBeginDate=%s" +
            "&txtEndDate=%s" +
            "&PAGENO=%d" +
            "&radioClass=00%%2C20%%2C30" +
            "&txtSite=all" +
            "&random=0.06480710955084468";
}
