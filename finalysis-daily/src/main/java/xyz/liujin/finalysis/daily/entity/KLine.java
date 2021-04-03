package xyz.liujin.finalysis.daily.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(autoResultMap = true)
public class KLine {
    @TableId(type = IdType.AUTO )
    private Integer id;
    private String stockCode;
    private LocalDate date;
    private BigDecimal open;
    private BigDecimal close;
    private BigDecimal high;
    private BigDecimal low;
    private BigDecimal change;
    private BigDecimal pctChange;
    private Long volume;
    private BigDecimal amount;
    private BigDecimal volumeRatio;
    private BigDecimal turn;
    private BigDecimal committee;
    private BigDecimal selling;
    private BigDecimal buying;
}
