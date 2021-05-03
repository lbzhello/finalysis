package xyz.liujin.finalysis.extractor.tushare;

import cn.hutool.core.bean.BeanUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.base.json.CsvMapper;
import xyz.liujin.finalysis.daily.entity.DailyIndicator;
import xyz.liujin.finalysis.extractor.tushare.api.Tushare;
import xyz.liujin.finalysis.extractor.tushare.api.TushareResp;
import xyz.liujin.finalysis.extractor.tushare.dto.TushareDailyIndicator;

import java.io.IOException;

/**
 * 获取股票每日指标
 */
@Component
public class DailyIndicatorExtractor {
    private static Logger logger = LoggerFactory.getLogger(DailyIndicatorExtractor.class);

    public Flux<DailyIndicator> extractDailyIndicator() {
        return Tushare.DailyBasic.builder()
                .ts_code("")
                .trade_date("20210430")
                .build()
                .req("")
                .flatMap(response -> {
                    try {
                        String bodyStr = response.body().string();
                        // 字段映射
                        return CsvMapper.create(TushareResp.FIELDS_PATH, TushareResp.ITEMS_PATH)
                                .eval(bodyStr, TushareDailyIndicator.class)
                                .map(this::toDailyIndicator);

                    } catch (IOException e) {
                        logger.error("failed to extract daily indicator", e);
                    }

                    return Flux.just();
                });
    }

    public DailyIndicator toDailyIndicator(TushareDailyIndicator tushareDailyIndicator) {
        DailyIndicator dailyIndicator = new DailyIndicator();
        BeanUtil.copyProperties(tushareDailyIndicator, dailyIndicator);
        return dailyIndicator;
    }
}
