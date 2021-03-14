package xyz.liujin.finalysis.start;

import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.base.util.LogUtils;
import xyz.liujin.finalysis.extractor.tushare.Tushare;

import java.util.Arrays;
import java.util.Optional;
import java.util.stream.Collectors;

public class LocalTest {
    @Test
    public void test() {
        String[] str = null;
        String collect = Optional.ofNullable(str)
                .stream()
                .flatMap(it -> Arrays.stream(it))
                .collect(Collectors.joining(","));
        System.out.println(collect);
    }
}
