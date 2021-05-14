package xyz.liujin.finalysis.extractor.tushare;

import cn.hutool.json.JSONUtil;
import org.junit.jupiter.api.Test;
import xyz.liujin.finalysis.base.util.DebugUtils;
import xyz.liujin.finalysis.extractor.tushare.api.Tushare;

import java.io.IOException;
import java.util.Map;

public class TushareTest {

    @Test
    public void stock() {
        Tushare.StockBasic.builder()
                .build()
                .req()
                .subscribe(it -> {
                    System.out.println(it);
                });
        DebugUtils.waitMillis(2000);
        System.out.println("hello");
    }

    @Test
    public void daily() {
        Tushare.Daily.builder()
                .trade_date("20210514")
                .ts_code("600309.SH")
                .build()
                .req()
                .subscribe(it -> {
                    try {
                        Map map = JSONUtil.toBean(it.body().string(), Map.class);
                        System.out.println();
                    } catch (IOException e) {


                    }
                });
        DebugUtils.waitMillis(2000);
    }

    /**
     * 股票日常指标获取
     */
    @Test
    public void dailyBasic() {
        Tushare.DailyBasic.builder()
                .build()
                .req()
                .subscribe(it -> {
                    try {
                        String body = it.body().string();
                        Map bodyMap = JSONUtil.toBean(body, Map.class);
                        System.out.println(body);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
        DebugUtils.waitMillis(2000);
    }
}
