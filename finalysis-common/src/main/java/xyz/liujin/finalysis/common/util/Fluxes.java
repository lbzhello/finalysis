package xyz.liujin.finalysis.common.util;

import reactor.core.publisher.Flux;

import java.util.Objects;

public class Fluxes {

    /**
     * 返回可空流
     * @param elem
     * @param <T>
     * @return
     */
    public static <T> Flux<T> nullable(T elem) {
        if (Objects.isNull(elem)) {
            return Flux.just();
        }

        return Flux.just(elem);
    }

    /**
     * 创建流，若含有空元素则用 empty 替代
     * @param iterable
     * @param empty
     * @param <T>
     * @return
     */
    public static <T> Flux<T> nullable(Iterable<T> iterable, T empty) {
        if (Objects.isNull(iterable)) {
            return Flux.just();
        }

        return Flux.create(fluxSink -> {
            for (T t : iterable) {
                if (Objects.nonNull(t)) {
                    fluxSink.next(t);
                } else {
                    fluxSink.next(empty);
                }
            }
            fluxSink.complete();
        });

    }

}
