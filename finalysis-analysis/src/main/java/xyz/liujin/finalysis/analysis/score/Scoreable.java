package xyz.liujin.finalysis.analysis.score;

import xyz.liujin.finalysis.analysis.entity.Score;

/**
 * 分数生成接口，实现此接口表示会生成一个分数码定义
 * @deprecated 使用 {@link xyz.liujin.finalysis.analysis.score.annotation.ScoreConfig} 替代
 */
@Deprecated
public interface Scoreable {
    /**
     * 返回分数对象，返回 null 表示使用注解方式生成 {@code Score}
     * @return
     * @see ScoreUtil
     */
    default Score getScore() {
        return null;
    }
}
