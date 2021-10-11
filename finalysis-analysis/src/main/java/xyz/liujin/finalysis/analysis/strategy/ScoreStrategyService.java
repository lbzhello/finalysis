package xyz.liujin.finalysis.analysis.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.entity.StockScore;
import xyz.liujin.finalysis.base.util.MyLogger;

import javax.annotation.Nullable;
import javax.annotation.PostConstruct;
import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
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
    private List<ScoreStrategy<? extends StrategyQo>> scoreStrategies;

    // 查询对象和计分策略之间的对应关系 qo -> ScoreStrategy
    private final Map<Class<? extends StrategyQo>, ScoreStrategy<? super StrategyQo>> strategyMap = new HashMap<>();

    @PostConstruct
    public void init() {
        Flux.fromIterable(scoreStrategies)
                // 计分策略和对应的查询类映射关系
                .subscribe(scoreStrategy -> {
                    logger.debug("find score strategy", "scoreStrategy", scoreStrategy);
                    Type genericSuperclass = scoreStrategy.getClass().getGenericSuperclass();
                    Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
                    Class<? extends StrategyQo> actualClass = (Class<? extends StrategyQo>) actualTypeArguments[0];
                    strategyMap.put(actualClass, (ScoreStrategy<? super StrategyQo>) scoreStrategy);
                });
    }

    /**
     * 根据策略查询类查到对应的策略类
     * @param strategyQoClass
     * @return
     */
    public ScoreStrategy<? super StrategyQo> getStrategy(Class<? extends StrategyQo> strategyQoClass) {
        return strategyMap.get(strategyQoClass);
    }

    /**
     * 计分策略调用分发接口，根据 {@code StrategyQo} 的实现类调用对应的策略
     * @param strategyQo
     * @return
     */
    public Flux<StockScore> score(@Nullable StrategyQo strategyQo) {
        if (Objects.isNull(strategyQo)) {
            return Flux.empty();
        }

        logger.debug("strategy condition", "strategyQo", strategyQo);
        ScoreStrategy<? super StrategyQo> scoreStrategy = getStrategy(strategyQo.getClass());
        if (Objects.isNull(scoreStrategy)) {
            logger.warn("can't find strategy", "strategyQo", strategyQo.getClass());
            return Flux.empty();
        }

        logger.info("score by strategy", "scoreStrategy", scoreStrategy.getClass());
        return scoreStrategy.score(strategyQo);
    }

    private static MethodHandle mh;
    static {
        try {
            mh = MethodHandles.lookup().findVirtual(ScoreStrategy.class, "score", MethodType.methodType(Flux.class, StrategyQo.class));
        } catch (NoSuchMethodException | IllegalAccessException e) {
            logger.error("", e);
        }
    }

    /**
     * 根据策略查询类查到对应的策略类
     * 反射方式调用
     * @param strategyQo
     * @return
     * @deprecated 使用 {@link #score(StrategyQo)} 方法
     */
    @Deprecated
    public Flux<StockScore> scoreOld(StrategyQo strategyQo) {
        if (Objects.isNull(strategyQo)) {
            return Flux.empty();
        }

        try {
            ScoreStrategy<?> strategy = strategyMap.get(strategyQo.getClass());
            if (Objects.isNull(strategy)) {
                logger.debug("can't find strategy", "strategyQo", strategyQo.getClass());
                return Flux.empty();
            }
            return  (Flux<StockScore>) mh.invoke(strategy, strategyQo);
        } catch (Throwable e) {
            logger.error("failed to score", e);
        }
        return Flux.empty();
    }

}
