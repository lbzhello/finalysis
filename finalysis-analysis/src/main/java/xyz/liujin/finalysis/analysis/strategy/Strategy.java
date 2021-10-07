package xyz.liujin.finalysis.analysis.strategy;

import reactor.core.publisher.Flux;

/**
 * 股票查询策略接口
 * 根据条件查询符合的股票
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/29
 */
public interface Strategy<QO extends StrategyQo> {
    /**
     * 筛选符合得分条件的股票
     * @param qo 查询条件
     * @return
     */
    Flux<String> findCodes(QO qo);
}
