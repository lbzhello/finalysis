package xyz.liujin.finalysis.analysis.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.entity.StockScore;
import xyz.liujin.finalysis.analysis.score.ScoreUtil;
import xyz.liujin.finalysis.analysis.service.StockScoreService;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.DailyApp;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 股票计分策略接口，根据计分条件筛选出指定股票，并计算得分
 * @param <QO> 策略查询对象泛型
 */
@Configuration
public abstract class ScoreStrategy<QO extends ScoreStrategyQo> {
    private static MyLogger logger = MyLogger.getLogger(ScoreStrategy.class);

    @Autowired
    private DailyApp dailyApp;

    @Autowired
    private StockScoreService stockScoreService;

    /**
     * 获取计分查询对象
     * @param scoreQo
     * @return
     */
    public abstract QO getQueryStrategy(ScoreQo scoreQo);

    /**
     * 筛选符合得分条件的股票
     * @param qo 查询条件
     * @return
     */
    public abstract Flux<String> findCodes(QO qo);

    /**
     * 股票计分
     * @param scoreQo
     * @return
     */
    public Flux<StockScore> score(ScoreQo scoreQo) {
        // 获取策略查询对象
        QO queryStrategy = getQueryStrategy(scoreQo);
        if (Objects.isNull(queryStrategy)) {
            logger.debug("condition is null or empty, skip");
            return Flux.empty();
        }

        logger.debug("score by strategy", "queryStrategy", queryStrategy);

        // 查询日期，默认数据库最新或当天
        LocalDate date = ObjectUtils.firstNonNull(scoreQo.getDate(), dailyApp.getLatestDate(), LocalDate.now());
        if (Objects.isNull(queryStrategy.getDate())) {
            queryStrategy.setDate(date);
        }
        // 默认全局分页信息
        if (Objects.isNull(queryStrategy.getPage()) && Objects.nonNull(scoreQo.getPage())) {
            queryStrategy.setPage(scoreQo.getPage());
        }

        // 计算得分
        Score score = ScoreUtil.getScore(queryStrategy);

        // 删除当日，具有该分数码的股票，因为每次计分输出的股票是不一样的
        stockScoreService.deleteByDateAndScoreCode(date, score.getScoreCode());

        return this.findCodes(queryStrategy)
                .map(stockCode -> StockScore.builder()
                        .date(date)
                        .stockCode(stockCode)
                        .scoreCode(score.getScoreCode())
                        .build());
    }
}
