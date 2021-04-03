package xyz.liujin.finalysis.stock.event;

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
public class StockChangeEvent {
    /**
     * 新增的股票代码
     */
    private List<String> addCodes;
}
