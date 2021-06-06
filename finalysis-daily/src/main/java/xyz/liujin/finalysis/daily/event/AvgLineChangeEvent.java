package xyz.liujin.finalysis.daily.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 *  均线更新事件（完成）
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AvgLineChangeEvent {
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
