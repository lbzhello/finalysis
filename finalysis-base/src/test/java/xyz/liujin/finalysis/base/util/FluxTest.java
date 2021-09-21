package xyz.liujin.finalysis.base.util;

import org.junit.jupiter.api.Test;
import reactor.core.publisher.Flux;

public class FluxTest {
    /**
     * consumer 发生异常
     */
    @Test
    public void consumerErrorTest() {
        Flux.just(1)
                .subscribe(it -> {
                    throw new IllegalStateException("error");
                }, e -> System.out.println("捕获异常：" + e));
    }
}
