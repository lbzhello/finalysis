package xyz.liujin.finalysis.base.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 *  K 线更新事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class KLineChangeEvent {
    /**
     * 开始时间
     */
    private LocalDate start;
    /**
     * 结束时间
     */
    private LocalDate end;
    /**
     * 股票列表
     */
    private List<String> codes;
}
