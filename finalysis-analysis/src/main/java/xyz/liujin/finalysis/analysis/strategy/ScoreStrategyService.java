package xyz.liujin.finalysis.analysis.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import javax.annotation.PostConstruct;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/10/10
 */
@Service
public class ScoreStrategyService {

    @Autowired
    private List<ScoreStrategy<? extends StrategyQo>> scoreStrategies;

    // 查询对象和计分策略之间的对应关系 qo -> ScoreStrategy
    private final Map<Class<? extends StrategyQo>, ScoreStrategy<? super StrategyQo>> strategyMap = new HashMap<>();

    @PostConstruct
    public void init() {
        Flux.fromIterable(scoreStrategies)
                // 计分策略和对应的查询类映射关系
                .subscribe(scoreStrategy -> {
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
}
