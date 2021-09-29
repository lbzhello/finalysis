package xyz.liujin.finalysis.analysis.strategy;

import xyz.liujin.finalysis.base.page.PageQo;

import java.time.LocalDate;

/**
 * 策略查询对象
 */
public interface StrategyQo {
    /**
     * 查询日期
     * @return
     */
    LocalDate getDate();

    void setDate(LocalDate date);

    /**
     * 分页信息
     * @return
     */
    PageQo getPage();

    void setPage(PageQo page);
}
