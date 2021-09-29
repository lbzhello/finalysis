package xyz.liujin.finalysis.daily.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import xyz.liujin.finalysis.base.util.DateUtils;
import xyz.liujin.finalysis.daily.entity.DailyIndicator;
import xyz.liujin.finalysis.daily.entity.KLine;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;

/**
 * daily 模块服务接口，对外外观类
 */
@Service
public class DailyService {
    private static final Logger logger = LoggerFactory.getLogger(DailyService.class);

    @Autowired
    private KLineService kLineService;

    @Autowired
    private DailyIndicatorService dailyIndicatorService;

    /**
     * 获取最近 limit 天交易日历
     * @param endDate 日历结束日期，默认最新
     * @param limit 最多返回记录数
     * @return
     */
    public List<LocalDate> tradingCalendar(@Nullable LocalDate endDate, int limit) {
        return kLineService.tradingCalendar(endDate, limit);
    }

    /**
     * 获取最新的数据日期
     * @return
     */
    public LocalDate getLatestDate() {
        return kLineService.getLatestDate();
    }

    /**
     * 检验数据完整性
     * @return 数据不正确的日期
     */
    public Flux<LocalDate> checkDataIntegrity(@Nullable LocalDate start, @Nullable LocalDate end) {
        // 默认当天
        if (Objects.isNull(start)) {
            start = LocalDate.now();
        }
        if (Objects.isNull(end)) {
            end = LocalDate.now();
        }
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
