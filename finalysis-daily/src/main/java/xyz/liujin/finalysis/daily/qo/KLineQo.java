package xyz.liujin.finalysis.daily.qo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class KLineQo {
    /**
     * 股票代码
     */
    private String code;

    /**
     * K 线类型：day
     */
    private String type;
}
