package xyz.liujin.finalysis.analysis.entity;

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
public class Recommend {
    @TableId(type = IdType.AUTO)
    private Integer id;
    private String stockCode;
    private LocalDate date;
    //最近最大量额，量比和成交额的乘积
    private BigDecimal volAmount;
}
