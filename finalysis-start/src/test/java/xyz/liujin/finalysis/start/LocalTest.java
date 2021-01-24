package xyz.liujin.finalysis.start;

import org.junit.jupiter.api.Test;

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
