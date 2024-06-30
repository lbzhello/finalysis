package com.liujin.finalysis.analysis.strategy;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.TurnRatioQo;
import xyz.liujin.finalysis.analysis.strategy.ScoreStrategy;
import xyz.liujin.finalysis.analysis.strategy.StrategyQo;
import xyz.liujin.finalysis.analysis.strategy.impl.AmountRatioStrategy;

import java.lang.invoke.MethodHandle;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;

public class ScoreStrategyServiceTest {
//    @Test
    public void methodHandleTest() throws Throwable {
        MethodHandle mh = MethodHandles.lookup().findVirtual(ScoreStrategy.class, "findCodes", MethodType.methodType(Flux.class, StrategyQo.class));
        Flux<String> o1 = (Flux<String>) mh.invoke(new AmountRatioStrategy(), new TurnRatioQo());

        o1.subscribe(it -> System.out.println(it));

        System.out.println();
    }
}
