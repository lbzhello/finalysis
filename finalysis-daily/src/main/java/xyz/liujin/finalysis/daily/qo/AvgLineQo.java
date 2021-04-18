package xyz.liujin.finalysis.daily.qo;

import lombok.*;
import xyz.liujin.finalysis.base.page.Page;

import java.time.LocalDate;
import java.util.List;

/**
 * 均线查询对象
 */
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AvgLineQo extends Page {
    private LocalDate start;
    private LocalDate end;
    private List<String> stockCodes;
    private Integer limit;
    private Integer offset;
}