package xyz.liujin.finalysis.daily.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@TableName(autoResultMap = true) // 查询时 typeHandler 不生效问题
public class HelloWorld {
    @TableId(type = IdType.AUTO )
    private Integer id;

    private Integer code;

    private String message;

    private LocalDate date;

}
