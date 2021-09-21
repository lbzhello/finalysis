package xyz.liujin.finalysis.analysis.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

/**
 * 定义标签具有的分数
 * 用于股票带有标签时的计分
 * @see xyz.liujin.finalysis.analysis.tag.Tagable
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(autoResultMap = true)
public class StockTag {
    @TableId(type = IdType.AUTO)
    private int id;
    // 股票带有标签时的日期
    private LocalDate date;
    // 股票代码
    private String stockCode;
    // 标签
    private String tag;
}
