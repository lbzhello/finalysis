package xyz.liujin.finalysis.analysis.strategy;

import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.entity.StockScore;
import xyz.liujin.finalysis.base.util.MyLogger;

import javax.annotation.Nullable;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/10/10
 */
@Service
public class ScoreStrategyService {
    private static final MyLogger logger = MyLogger.getLogger(ScoreStrategyService.class);

    @Autowired
    private Map<String, ScoreStrategy<? extends StrategyQo>> scoreStrategies;

    /**
     * 根据策略查询类查找对应的策略类
     * @param strategyQo
     * @return
     */
    public @Nullable ScoreStrategy<? extends StrategyQo> findStrategy(StrategyQo strategyQo) {
        if (Objects.isNull(strategyQo)) {
            return null;
        }

        ScoreStrategy<? extends StrategyQo> scoreStrategy = scoreStrategies.get(strategyQo.getType());
        if (Objects.isNull(scoreStrategy)) {
            logger.warn("can't find strategy", "strategyQo", strategyQo.getClass());
            return null;
        }
        return scoreStrategy;
    }

    /**
     * 策略选股
     * @param strategyQo
     * @return
     */
    public <QO extends StrategyQo> Flux<String> findCodes(QO strategyQo) {
        logger.debug("strategy condition", "strategyQo", strategyQo);
        ScoreStrategy<? super StrategyQo> strategy = (ScoreStrategy<? super StrategyQo>) findStrategy(strategyQo);
        if (Objects.isNull(strategy)) {
            return Flux.empty();
        }

        logger.info("score by strategy", "scoreStrategy", strategy.getClass());
        return strategy.findCodes(strategyQo);
    }

    /**
     * 根据不同的计分策略统计分数
     * @param strategies
     * @return
     */
    public Flux<StockScore> score(Flux<? extends StrategyQo> strategies) {
        return strategies
                // 调用对应的计分策略类
                .flatMap(this::score);
    }

    /**
     * 计分策略调用分发接口，根据 {@code StrategyQo} 的实现类调用对应的策略
     * @param strategyQo
     * @return
     */
    public <QO extends StrategyQo> Flux<StockScore> score(QO strategyQo) {
        logger.debug("strategy condition", "strategyQo", strategyQo);
        ScoreStrategy<? super StrategyQo> scoreStrategy = (ScoreStrategy<? super StrategyQo>) findStrategy(strategyQo);
        if (Objects.isNull(scoreStrategy)) {
            return Flux.empty();
        }

        logger.info("score by strategy", "scoreStrategy", scoreStrategy.getClass());
        return scoreStrategy.score(strategyQo);
    }

}
