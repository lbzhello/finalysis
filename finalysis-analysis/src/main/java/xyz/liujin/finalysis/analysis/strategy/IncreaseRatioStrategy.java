package xyz.liujin.finalysis.analysis.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.IncreaseRatioQo;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.entity.StockScore;
import xyz.liujin.finalysis.analysis.mapper.IncreaseRatioMapper;
import xyz.liujin.finalysis.analysis.score.ScoreUtil;
import xyz.liujin.finalysis.analysis.service.StockScoreService;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.service.KLineService;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 增幅比值指标
 * 最近 recDays 天增幅，与过去 hisDays 天增幅的比值
 * 数值大，说明最近几天股票增幅巨大，过去几天涨幅较小，龙抬头
 * 说明股票可能有重大利好消息
 */
@Service
public class IncreaseRatioStrategy implements Strategy<IncreaseRatioQo> {
    private static final MyLogger logger = MyLogger.getLogger(IncreaseRatioStrategy.class);

    @Autowired
    private IncreaseRatioMapper increaseRatioMapper;

    @Autowired
    private KLineService kLineService;

    @Autowired
    private StockScoreService stockScoreService;

    @Override
    public Flux<String> findCodes(IncreaseRatioQo increaseRatioQo) {
        if (Objects.isNull(increaseRatioQo)) {
            logger.debug("condition is null or empty, skip");
            return Flux.empty();
        }

        // 日期默认数据库最新
        increaseRatioQo.setDate(ObjectUtils.firstNonNull(increaseRatioQo.getDate(),
                kLineService.getLatestDate(),
                LocalDate.now()));

        return Flux.fromIterable(increaseRatioMapper.findCodes(increaseRatioQo));
    }

    @Override
    public Flux<StockScore> score(ScoreQo scoreQo) {
        // 增幅比指标
        IncreaseRatioQo increaseRatioQo = scoreQo.getIncreaseRatio();
        if (Objects.isNull(increaseRatioQo)) {
            logger.debug("condition is null or empty, skip");
            return Flux.empty();
        }

        logger.debug("score by strategy", "increaseRatioQo", increaseRatioQo);

        // 推荐日期，默认数据库最新或当天
        LocalDate date = ObjectUtils.firstNonNull(scoreQo.getDate(), kLineService.getLatestDate(), LocalDate.now());
        if (Objects.isNull(increaseRatioQo.getDate())) {
            increaseRatioQo.setDate(date);
        }
        // 默认全局分页信息
        if (Objects.isNull(increaseRatioQo.getPage()) && Objects.nonNull(scoreQo.getPage())) {
            increaseRatioQo.setPage(scoreQo.getPage());
        }

        // 计算得分
        Score score = ScoreUtil.getScore(increaseRatioQo);

        // 删除当日，具有该分数码的股票，因为每次计分输出的股票是不一样的
        stockScoreService.deleteByDateAndScoreCode(date, score.getScoreCode());

        return this.findCodes(increaseRatioQo)
                .map(stockCode -> StockScore.builder()
                        .date(date)
                        .stockCode(stockCode)
                        .scoreCode(score.getScoreCode())
                        .build());
    }
}
