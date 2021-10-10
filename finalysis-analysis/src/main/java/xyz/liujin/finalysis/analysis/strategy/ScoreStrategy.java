package xyz.liujin.finalysis.analysis.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.entity.StockScore;
import xyz.liujin.finalysis.analysis.service.ScoreService;
import xyz.liujin.finalysis.analysis.service.StockScoreService;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.daily.service.DailyService;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 股票计分策略抽象接口，根据计分条件筛选出指定股票，并计算得分
 * @param <QO> 策略查询对象泛型
 */
@Configuration
public abstract class ScoreStrategy<QO extends StrategyQo> implements Strategy<QO> {
    private static MyLogger logger = MyLogger.getLogger(ScoreStrategy.class);

    @Autowired
    private DailyService dailyService;

    @Autowired
    private StockScoreService stockScoreService;

    @Autowired
    private ScoreService scoreService;

    /**
     * 股票计分
     *
     * @param strategyQo
     * @return
     */
    public Flux<StockScore> score(QO strategyQo) {
        logger.debug("score strategy", "strategy", strategyQo);

        // 查询日期，默认数据库最新或当天
        LocalDate date = dailyService.getLatestDateOrNow();
        if (Objects.isNull(strategyQo.getDate())) {
            strategyQo.setDate(date);
        }

        // 计算得分
        Score score = scoreService.getScore(strategyQo);
        if (Objects.isNull(score)) {
            logger.debug("can't get score, stop scoring");
            return Flux.empty();
        }

        // 删除当日，具有该分数码的股票，因为每次计分输出的股票是不一样的
        stockScoreService.deleteByDateAndScoreCode(date, score.getScoreCode());

        return this.findCodes(strategyQo)
                .map(stockCode -> StockScore.builder()
                        .date(date)
                        .stockCode(stockCode)
                        .scoreCode(score.getScoreCode())
                        .build());

    }

}
