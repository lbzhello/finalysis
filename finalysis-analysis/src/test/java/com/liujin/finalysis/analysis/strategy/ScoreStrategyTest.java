package com.liujin.finalysis.analysis.strategy;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.strategy.impl.TurnRatioStrategy;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/10/10
 */
public class ScoreStrategyTest {
    /**
     * field class 对象
     */
    @Test
    public void fieldTest() {
        ScoreQo scoreQo = new ScoreQo();
        Class<? extends ScoreQo> scoreQoClass = scoreQo.getClass();
        Flux.fromArray(scoreQoClass.getDeclaredFields())
                .subscribe(field -> {
                    System.out.println(field);
                });
    }

    /**
     * 获取泛型参数
     */
    @Test
    public void genericTypeTest() {
        TurnRatioStrategy turnRatioStrategy = new TurnRatioStrategy();
        Type genericSuperclass = turnRatioStrategy.getClass().getGenericSuperclass();
        Type[] actualTypeArguments = ((ParameterizedType) genericSuperclass).getActualTypeArguments();
        Class<?> actualClass = (Class<?>) actualTypeArguments[0];
        System.out.println();
    }
}
