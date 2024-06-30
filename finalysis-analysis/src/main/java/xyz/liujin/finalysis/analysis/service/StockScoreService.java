package xyz.liujin.finalysis.analysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.entity.StockScore;
import xyz.liujin.finalysis.analysis.mapper.StockScoreMapper;
import xyz.liujin.finalysis.analysis.strategy.ScoreStrategyService;
import xyz.liujin.finalysis.analysis.strategy.StrategyQo;
import xyz.liujin.finalysis.base.page.PageQo;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.entity.DailyIndicator;
import xyz.liujin.finalysis.daily.service.DailyIndicatorService;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Service
public class StockScoreService extends ServiceImpl<StockScoreMapper, StockScore> implements IService<StockScore> {
    private static final MyLogger logger = MyLogger.getLogger(StockScoreService.class);

    @Autowired
    private ScoreStrategyService scoreStrategyService;

    @Autowired
    private DailyIndicatorService dailyIndicatorService;

    /**
     * 根据条件计算股票得分并入库
     * @return
     */
    public Flux<StockScore> scoreAndSave(ScoreQo scoreQo) {
        logger.debug("start to score", "scoreQO", scoreQo);

        LocalDate date = ObjectUtils.firstNonNull(scoreQo.getDate(), LocalDate.now());
        PageQo pageQo = ObjectUtils.firstNonNull(scoreQo.getPage(), PageQo.builder()
                        .limit(100)
                        .offset(0)
                        .build());

        // scoreQo 转换成策略查询对象
        List<StrategyQo> strategies = new ArrayList<>();
        Class<? extends ScoreQo> scoreQoClass = scoreQo.getClass();
        Flux.fromArray(scoreQoClass.getDeclaredFields())
                .subscribe(field -> {
                    try {
                        // 如果是策略查询类测加入列表
                        Class<?> type = field.getType();
                        if (StrategyQo.class.isAssignableFrom(type)) {
                            field.setAccessible(true);
                            StrategyQo strategyQo = (StrategyQo) field.get(scoreQo);
                            if (Objects.nonNull(strategyQo)) {
                                strategyQo.setDate(date);
                                strategyQo.setPage(pageQo);
                                strategies.add(strategyQo);
                            }
                        }
                    } catch (IllegalAccessException e) {
                        logger.error("failed to retrieve strategyQo from scoreQo", e);
                    }
                });

        return scoreAndSave(Flux.fromIterable(strategies));
    }

    /**
     * 根据不同的策略计分并入库
     * @param strategies
     * @return
     */
    public Flux<StockScore> scoreAndSave(Flux<? extends StrategyQo> strategies) {
        return scoreStrategyService.score(strategies)
                .map(stockScore -> {
                    logger.trace("get stock score", "stockScore", stockScore);
                    save(stockScore);
                    return stockScore;
                });
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
