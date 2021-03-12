package xyz.liujin.finalysis.base.event;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 股票更新事件
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class StockRefreshEvent {
    /**
     * 新增的股票代码
     */
    private List<String> addCodes;
}
