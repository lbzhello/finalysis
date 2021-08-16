package xyz.liujin.finalysis.daily.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import xyz.liujin.finalysis.base.util.DateUtils;
import xyz.liujin.finalysis.daily.entity.DailyIndicator;
import xyz.liujin.finalysis.daily.entity.KLine;
import xyz.liujin.finalysis.stock.service.StockService;

import java.time.LocalDate;
import java.util.Date;
import java.util.Objects;
import java.util.function.Consumer;

@Service
public class DailyService {
    private static final Logger logger = LoggerFactory.getLogger(DailyService.class);

    @Autowired
    private StockService stockService;

    @Autowired
    private KLineService kLineService;

    @Autowired
    private DailyIndicatorService dailyIndicatorService;

    /**
     * 检验数据完整性
     * @return 数据不正确的日期
     */
    public Flux<LocalDate> checkDataIntegrity(LocalDate start, LocalDate end) {
        return DateUtils.iterateDays(start, end)
                .flatMap(date -> Flux.create((Consumer<FluxSink<LocalDate>>) fluxSink -> {
                    Integer kCount = kLineService.lambdaQuery().eq(KLine::getDate, date).count();
                    Integer dCount = dailyIndicatorService.lambdaQuery().eq(DailyIndicator::getDate, date).count();
                    // 日线数据和指标数据应该一样
                    if (!Objects.equals(kCount, dCount)) {
                        logger.error("data lack: date {} k_line total {} daily_indicator total {}", date, kCount, dCount);
                        fluxSink.next(date);
                    }

                    fluxSink.complete();
                }));
    }
}
