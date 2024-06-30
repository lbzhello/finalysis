package xyz.liujin.finalysis.analysis.score;

/**
 * 分数类型表
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/25
 * @see xyz.liujin.finalysis.analysis.entity.Score
 * @see xyz.liujin.finalysis.analysis.score.Scoreable
 */
public class ScoreType {
    // 增幅比指标，最近几日增幅，与过去几日增幅比值；增幅比大，表示股票开始向上突破
    public static final String INCREASE_RATIO = "increase_ratio";
    // 换手比指标，最近几日换手率，与过去几日换手率比值；换手比大，说明股票开始异动
    public static final String TURN_RATIO = "turn_ratio";
    // 最低价格支撑指标，当前股价没有跌破最近最低价格，说明股票开始企稳，若在突破初期，往往意味着洗盘结束
    public static final String MINIMUM_PRICE_SUPPORT = "minimum_price_support";
    // 成交额比指标，最近几日平均成交额，与过去几日平均成交额比值；成交额比大，说明股票开始放量
    public static final String AMOUNT_RATIO = "amount_ratio";
}
