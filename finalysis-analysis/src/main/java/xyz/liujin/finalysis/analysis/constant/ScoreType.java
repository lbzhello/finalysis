package xyz.liujin.finalysis.analysis.constant;

/**
 * 分数类型表
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/25
 * @see xyz.liujin.finalysis.analysis.entity.Score
 * @see xyz.liujin.finalysis.analysis.score.Scoreable
 */
public enum ScoreType {
    // 增幅比指标
    INCREASE_RATIO("increase_ratio"),
    ;
    private String name;

    ScoreType(String name) {
        this.name = name;
    }

    public String getType() {
        return name;
    }
}
