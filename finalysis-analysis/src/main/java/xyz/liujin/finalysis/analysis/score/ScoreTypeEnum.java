package xyz.liujin.finalysis.analysis.score;

/**
 * 分数类型表
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/25
 * @see xyz.liujin.finalysis.analysis.entity.Score
 * @see xyz.liujin.finalysis.analysis.score.Scoreable
 */
public enum ScoreTypeEnum {
    // 增幅比指标
    INCREASE_RATIO("increase_ratio"),
    // 换手比指标
    TURN_RATIO("turn_ratio"),
    ;
    private String name;

    ScoreTypeEnum(String name) {
        this.name = name;
    }

    public String getType() {
        return name;
    }
}
