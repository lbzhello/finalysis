package xyz.liujin.finalysis.analysis.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.entity.StockScore;
import xyz.liujin.finalysis.analysis.service.ScoreService;
import xyz.liujin.finalysis.analysis.service.StockScoreService;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.service.DailyService;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 股票计分策略接口，根据计分条件筛选出指定股票，并计算得分
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
     * 获取计分查询对象
     * @param scoreQo
     * @return
     */
    public abstract QO getScoreable(ScoreQo scoreQo);

    /**
     * 股票计分
     * @param scoreQo
     * @return
     */
    public Flux<StockScore> score(ScoreQo scoreQo) {
        // 获取策略查询对象
        QO scoreable = getScoreable(scoreQo);
        if (Objects.isNull(scoreable)) {
            logger.debug("condition is null or empty, skip");
            return Flux.empty();
        }

        logger.debug("score on condition", "condition", scoreable);

        // 查询日期，默认数据库最新或当天
        LocalDate date = ObjectUtils.firstNonNull(scoreQo.getDate(), dailyService.getLatestDate(), LocalDate.now());
        if (Objects.isNull(scoreable.getDate())) {
            scoreable.setDate(date);
        }
        // 默认全局分页信息
        if (Objects.isNull(scoreable.getPage()) && Objects.nonNull(scoreQo.getPage())) {
            scoreable.setPage(scoreQo.getPage());
        }

        // 计算得分
        Score score = scoreService.getScore(scoreable);

        if (Objects.isNull(score)) {
            logger.debug("can't get score, stop scoring");
            return Flux.empty();
        }

        // 删除当日，具有该分数码的股票，因为每次计分输出的股票是不一样的
        stockScoreService.deleteByDateAndScoreCode(date, score.getScoreCode());

        return this.findCodes(scoreable)
                .map(stockCode -> StockScore.builder()
                        .date(date)
                        .stockCode(stockCode)
                        .scoreCode(score.getScoreCode())
                        .build());
    }
}
