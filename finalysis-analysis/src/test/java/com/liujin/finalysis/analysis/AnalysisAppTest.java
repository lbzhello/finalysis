package com.liujin.finalysis.analysis;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/25
 */
public class AnalysisAppTest {
    @Test
    public void fluxEmptyTest2() {
        Flux.fromIterable(List.of(Flux.just(1, 2, 3), Flux.<Integer>empty(), Flux.just(4, 5, 6)))
                .flatMap(it -> it)
                .subscribe(it -> {
                    System.out.println(it);
                });
    }

    @Test
    public void fluxEmptyTest() {
        Flux.just(1, 2, 3)
                .concatWith(Flux.empty())
                .concatWith(Flux.just(4, 5, 6))
                .subscribe(it -> {
                    System.out.println(it);
                });
    }
}
