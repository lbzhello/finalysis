package xyz.liujin.finalysis.analysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.entity.StockScore;
import xyz.liujin.finalysis.analysis.mapper.StockScoreMapper;
import xyz.liujin.finalysis.analysis.strategy.IncreaseRatioStrategy;
import xyz.liujin.finalysis.analysis.strategy.QueryStrategy;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.daily.service.KLineService;

import java.time.LocalDate;
import java.util.List;

@Service
public class StockScoreService extends ServiceImpl<StockScoreMapper, StockScore> implements IService<StockScore> {
    private static final MyLogger logger = MyLogger.getLogger(StockScoreService.class);

    @Autowired
    private KLineService kLineService;

    @Autowired
    private IncreaseRatioStrategy increaseRatioStrategy;

    @Autowired
    private ScoreService scoreService;

    @Autowired
    private List<QueryStrategy<?>> st;

    /**
     * 根据条件计算股票得分
     * @return
     */
    public Mono<Integer> score(ScoreQo scoreQo) {
        logger.debug("start to score", "scoreQO", scoreQo);
        increaseRatioStrategy.score(scoreQo)
                .map(stockScore -> {
                    save(stockScore);
                    return 1;
                })
                .reduce(Integer::sum)
                .subscribe(count -> logger.debug("increaseRatioStrategy", "count", count),
                        e -> logger.error("failed to score increaseRatio", e));
        return Mono.empty();
    }

    /**
     * 删除当日，具有该分数码的股票，因为每次计分输出的股票是不一样的
     * @param date
     * @param tag
     */
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
