package xyz.liujin.finalysis.daily.entity;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
//@AllArgsConstructor
@TableName(autoResultMap = true) // 查询时 typeHandler 不生效问题
public class DailyIndicator {
}
