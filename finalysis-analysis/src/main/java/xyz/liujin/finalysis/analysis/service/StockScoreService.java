package xyz.liujin.finalysis.analysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.analysis.dto.IncreaseRatioQo;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.entity.StockScore;
import xyz.liujin.finalysis.analysis.mapper.StockScoreMapper;
import xyz.liujin.finalysis.analysis.strategy.IncreaseRatioStrategy;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.service.KLineService;

import java.time.LocalDate;
import java.util.Objects;

@Service
public class StockScoreService extends ServiceImpl<StockScoreMapper, StockScore> implements IService<StockScore> {
    private static final MyLogger logger = MyLogger.getLogger(StockScoreService.class);

    @Autowired
    private KLineService kLineService;

    @Autowired
    private IncreaseRatioStrategy increaseRatioStrategy;

    @Autowired
    private ScoreService scoreService;

    /**
     * 股票统计分数
     * @return
     */
    public Mono<Integer> score(ScoreQo scoreQo) {
        logger.debug("start to score", "scoreQO", scoreQo);
        // 推荐日期，默认数据库最新或当天
        LocalDate date = ObjectUtils.firstNonNull(scoreQo.getDate(), kLineService.getLatestDate(), LocalDate.now());

        // 增幅比指标
        IncreaseRatioQo increaseRatio = scoreQo.getIncreaseRatio();
        if (Objects.nonNull(increaseRatio) && Objects.isNull(increaseRatio.getDate())) {
            increaseRatio.setDate(date);
        }
        // 默认全局分页信息
        if (Objects.isNull(increaseRatio.getPage()) && Objects.nonNull(scoreQo.getPage())) {
            increaseRatio.setPage(scoreQo.getPage());
        }

        // 计分入库
        logger.debug("score increase ratio strategy", "increaseRatio", increaseRatio);

        // 分数码入库
        Score score = scoreService.getScore(increaseRatio);

        // 删除当日，具有该分数码的股票，因为每次计分输出的股票是不一样的
        deleteByDateAndScoreCode(date, score.getScoreCode());

        increaseRatioStrategy.findCodes(increaseRatio)
                .map(stockCode -> {
                    return StockScore.builder()
                            .date(date)
                            .stockCode(stockCode)
                            .scoreCode(score.getScoreCode())
                            .build();
                })
                .map(stockScore -> {
                    save(stockScore);
                    return 1;
                })
                .reduce(Integer::sum)
                .subscribe(count -> logger.debug("increaseRatioStrategy", "count", count),
                        e -> logger.error("failed to score increaseRatio", e));
        return Mono.empty();
    }

    public void deleteByDateAndScoreCode(LocalDate date, String tag) {
        getBaseMapper().deleteByDateAndTag(date, tag);
    }

    public void saveIfNotExist(StockScore stockScore) {
        lambdaQuery().eq(StockScore::getDate, stockScore.getDate())
                .eq(StockScore::getStockCode, stockScore.getStockCode())
                .eq(StockScore::getScoreCode, stockScore.getScoreCode())
                .oneOpt()
                // 存在忽略，不存在新增
                .ifPresentOrElse(it -> {}, () -> {
                    save(stockScore);
                });
    }
}
