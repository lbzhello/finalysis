package xyz.liujin.finalysis.analysis.qo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

/**
 * 均线查询对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvgLineQo {
    private LocalDate startDate;
    private LocalDate endDate;
    private List<String> stockCodes;
}
