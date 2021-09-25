package xyz.liujin.finalysis.analysis.score;

import xyz.liujin.finalysis.analysis.entity.Score;

/**
 * 分数生成接口，实现此接口表示会生成一个分数码定义
 */
public interface Scoreable {
    Score getScore();
}
