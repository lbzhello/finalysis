package xyz.liujin.finalysis.analysis.strategy;

import xyz.liujin.finalysis.base.page.PageQo;

import java.time.LocalDate;

/**
 * 股票筛选策略，查询对象抽象接口
 */
public interface QueryStrategyQo {
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
