package xyz.liujin.finalysis.analysis.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 推荐表更新事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class RecommendChangeEvent {
    /**
     * 日期
     */
    private LocalDate date;
    /**
     * 股票列表
     */
    private List<String> codes;
}
