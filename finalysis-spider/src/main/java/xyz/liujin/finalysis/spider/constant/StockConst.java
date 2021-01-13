package xyz.liujin.finalysis.spider.constant;

/**
 * 股票中常亮
 */
public class StockConst {
    /**
     * 连续两年股东收益为负
     */
    public static final String ST = "ST";
    /**
     * 标识有退市风险股票
     */
    public static final String STAR_ST = "*ST";

    /**
     * 退市/停牌标识？
     */
    public static final String STAR = "*";

    /**
     * 空或 null 字符串转成金额时，当做 0 处理
     */
    public static final String ZERO = "0";

    /**
     * 上海证券交易所简称
     */
    public static final String SH = "SH";

    /**
     * 深圳证券交易所简称
     */
    public static final String SZ = "SZ";

    /**
     * 有些股票代码上面含有 .
     */
    public static final String POINT = ".";

    // 股票状态
    // 正常
    public static final int NORMAL = 0;
    // 退市
    public static final int DE_LISTING = -1;
    // 暂停上市
    public static final int PAUSE_LISTING = 1;
}
