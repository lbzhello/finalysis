package xyz.liujin.finalysis.analysis.entity;

import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.OffsetDateTime;

/**
 * 定义标签
 * 带有分数
 * 用于股票带有标签时的计分
 * @see xyz.liujin.finalysis.analysis.tag.Tagable
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(autoResultMap = true)
public class TagScore {
    // 标签名
    @TableId
    private String tag;
    // 具有标签股票得分
    private int score;
    // 标签描述
    private String tagDesc;
    // 标签类型
    private String type;

    private OffsetDateTime createTime;

    private OffsetDateTime updateTime;
}
