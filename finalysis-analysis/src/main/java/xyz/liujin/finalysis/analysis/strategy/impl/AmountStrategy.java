package xyz.liujin.finalysis.analysis.strategy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.TurnRatioQo;
import xyz.liujin.finalysis.analysis.strategy.ScoreStrategy;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.daily.service.DailyService;

/**
 * 成交额策略
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/27
 */
public class AmountStrategy extends ScoreStrategy<TurnRatioQo> {
    private static final MyLogger logger = MyLogger.getLogger(AmountStrategy.class);

    @Autowired
    private DailyService dailyService;

    @Override
    public Flux<String> findCodes(TurnRatioQo turnRatioQo) {
        // 默认数据库最新日期
        turnRatioQo.setDate(dailyService.getLatestDateOrNow(turnRatioQo.getDate()));

        return Flux.empty();
    }

}
