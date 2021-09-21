package xyz.liujin.finalysis.base.util;

import org.junit.jupiter.api.Test;

public class MyLoggerTest {

    @Test
    public void fluxTest() {
        MyLogger logger = MyLogger.getLogger(MyLoggerTest.class);
        logger.debug("hello world", "p1", 2333, "p2", "nisxhi sfsd");
        System.out.println();
    }

    public void arrParams(String... params) {
        System.out.println(params);
    }
}
