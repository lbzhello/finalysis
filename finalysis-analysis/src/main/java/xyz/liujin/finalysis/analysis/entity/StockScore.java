package xyz.liujin.finalysis.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.analysis.score.Scoreable;

import java.time.LocalDate;

/**
 * 股票得分表
 * 用于股票根据分数代码统计分数
 * @see Scoreable
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(autoResultMap = true)
public class StockScore {
    @TableId(type = IdType.AUTO)
    private Integer id;
    // 股票得分日期
    private LocalDate date;
    // 股票代码
    private String stockCode;
    // 分数代码
    private String scoreCode;
}
