package xyz.liujin.finalysis.analysis.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.analysis.constant.ScoreType;
import xyz.liujin.finalysis.analysis.score.Scoreable;

import java.time.OffsetDateTime;

/**
 * 分数定义表
 * 用于股票行情计分
 * @see Scoreable
 * @see ScoreType
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(autoResultMap = true)
public class Score {
    // 分数代码，具有分数代码的股票，将具有相应的分数
    @TableId
    private String scoreCode;
    // 具有分数码的股票得分
    private int score;
    // 分数码说明
    private String description;
    // 分数码类型
    private String type;

    private OffsetDateTime createTime;

    private OffsetDateTime updateTime;
}
