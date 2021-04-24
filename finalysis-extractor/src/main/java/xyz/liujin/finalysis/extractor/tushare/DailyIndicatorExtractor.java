package xyz.liujin.finalysis.extractor.tushare;

import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.daily.entity.DailyIndicator;
import xyz.liujin.finalysis.extractor.tushare.api.Tushare;

/**
 * 获取股票每日指标
 */
@Component
public class DailyIndicatorExtractor {

    public Flux<DailyIndicator> extractDailyIndicator() {
        return Tushare.DailyBasic.builder()
                .ts_code("")
                .build()
                .req("")
                .map(response -> {
                    return DailyIndicator.builder().build();
                });
    }
}
