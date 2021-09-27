package xyz.liujin.finalysis.analysis.strategy;

import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.entity.StockScore;

/**
 * 股票分析策略接口，根据条件删选出指定股票
 */
public interface Strategy<QO> {
    /**
     * 筛选符合策略条件的股票
     * @param qo 查询条件
     * @return
     */
    Flux<String> findCodes(QO qo);

    /**
     * 股票计分
     * @param scoreQo
     * @return
     */
    default Flux<StockScore> score(ScoreQo scoreQo) {
        return Flux.empty();
    }
}
