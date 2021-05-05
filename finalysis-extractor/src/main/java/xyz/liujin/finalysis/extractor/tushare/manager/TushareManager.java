package xyz.liujin.finalysis.extractor.tushare.manager;

import cn.hutool.core.collection.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.base.executor.TaskPool;
import xyz.liujin.finalysis.daily.event.DailyIndicatorChangeEvent;
import xyz.liujin.finalysis.daily.event.KLineChangeEvent;
import xyz.liujin.finalysis.daily.service.DailyIndicatorService;
import xyz.liujin.finalysis.daily.service.KLineService;
import xyz.liujin.finalysis.extractor.tushare.DailyIndicatorExtractor;
import xyz.liujin.finalysis.extractor.tushare.TushareExtractor;
import xyz.liujin.finalysis.stock.entity.Stock;
import xyz.liujin.finalysis.stock.event.StockChangeEvent;
import xyz.liujin.finalysis.stock.service.StockService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 股票数据提取控制器
 */
@Service
public class TushareManager {
    private static Logger logger = LoggerFactory.getLogger(TushareManager.class);

    @Autowired
    private TushareExtractor tushareExtractor;

    @Autowired
    private StockService stockService;

    @Autowired
    private KLineService kLineService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private DailyIndicatorExtractor dailyIndicatorExtractor;

    @Autowired
    private DailyIndicatorService dailyIndicatorService;

    /**
     * 自动更新股票数据
     * 1. 更新股票数据
     * 2. 更新 K 线数据
     * @return
     */
    public Flux<String> refreshAll() {
        return Flux.merge(refreshStock(), refreshKLine(null, null, null), refreshDailyIndicator(null, null, null));
    }

    /**
     * 更新股票信息
     * @return
     */
    public Flux<String> refreshStock() {
        return Flux.create(sink -> {
            logger.debug("start extract stock {}", tushareExtractor.getClass());

            sink.next("start to extract stock. ");

            tushareExtractor.extractStock()
                    .subscribeOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                    // 获取新增的股票
                    .filter(stock -> {
                        Stock exist = stockService.getById(stock.getStockCode());
                        // 存在更新
                        if (Objects.nonNull(exist)) {
                            stockService.updateById(stock);
                        } else {
                            stockService.save(stock);
                        }
                        return Objects.isNull(exist);
                    })
                    .map(Stock::getStockCode)
                    .collectList()
                    .subscribeOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                    .subscribe(codes -> {
                        logger.debug("refresh stock success, add {}", codes.size());
                        applicationContext.publishEvent(StockChangeEvent.builder()
                                .addCodes(codes)
                                .build());
                    }, e -> logger.error("failed to extract stock", e));

            sink.next("<refreshStock> job running... ");
            sink.complete();
        });
    }

    /**
     * 爬取 K 线入库，更新股票信息
     * @param start 爬取开始日期，默认数据库最新数据
     * @param end 爬取结束日期，默认当天
     * @param stockCodes 需要爬取的股票列表，默认所有
     * @return
     */
    public Flux<String> refreshKLine(@Nullable LocalDate start, @Nullable LocalDate end, @Nullable List<String> stockCodes) {
        return Flux.create(sink -> {
            logger.debug("start refreshKLine. class: {}", tushareExtractor.getClass());

            // yyyy-MM-dd
            LocalDate startDate = Optional.ofNullable(start).orElseGet(() -> {
                // 需要更新的日期，数据库最新
                LocalDate latestDate = Optional.ofNullable(kLineService.getNextDate()).orElse(LocalDate.now());
                return latestDate;
            });
            // 默认当前日期
            LocalDate endDate = Optional.ofNullable(end).orElse(LocalDate.now());
            // 股票代码，默认所有股票
            List<String> codes = CollectionUtil.isNotEmpty(stockCodes) ? stockCodes : stockService.list().stream()
                            .map(Stock::getStockCode)
                            .collect(Collectors.toList());

            sink.next("start to extract k line. ");
            logger.debug("start to extract k line [startDate:{}, endDate:{}]", startDate, endDate);

            tushareExtractor.extractKLine(startDate, endDate, codes)
                    .subscribeOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                    .parallel(TaskPool.availableProcessors())
                    .runOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                    // 入库
                    .map(kLine -> {
                        kLineService.saveOrUpdate(kLine);
                        return 1;
                    })
                    // 统计更新条目；到这里任务已经执行完毕
                    .reduce(Integer::sum)
                    .subscribe(count -> {
                        logger.debug("refresh k line success, refreshed {}", count);
                        applicationContext.publishEvent(KLineChangeEvent.builder()
                                .start(startDate)
                                .end(endDate)
                                .codes(codes)
                                .build());
                        }, e -> logger.error("failed to extract k line", e));

            sink.next("<refreshKLine> job running... ");
            sink.complete();
        });
    }

    /**
     * 更新股票日指标数据
     * @param start 开始日期；默认从数据库获取最新日期的下一个日期
     * @param end   结束日期；默认当天
     * @param codes 股票列表；默认所有
     * @return
     */
    public Flux<String> refreshDailyIndicator(@Nullable LocalDate start, @Nullable LocalDate end, @Nullable List<String> codes) {
        return Flux.create(sink -> {
            logger.debug("start refresh daily indicator {}", tushareExtractor.getClass());
            sink.next("start to extract daily indicator");
            // 默认从数据库获取最新日期的下一个日期
            LocalDate startDate = Optional.ofNullable(start).orElse(dailyIndicatorService.getNextDate());
            // 默认当天
            LocalDate endDate = Optional.ofNullable(end).orElse(LocalDate.now());
            // 默认数据库所有股票
            List<String> stockCodes = CollectionUtil.isNotEmpty(codes) ? codes : stockService.list().stream()
                    .map(Stock::getStockCode)
                    .collect(Collectors.toList());

            dailyIndicatorExtractor.extractDailyIndicator(startDate, endDate, stockCodes)
                    .parallel(TaskPool.availableProcessors())
                    .runOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                    // 入库
                    .map(dailyIndicator -> {
                        dailyIndicatorService.saveByCodeDate(dailyIndicator);
                        return 1;
                    })
                    .reduce(Integer::sum)
                    .subscribe(count -> {
                        logger.debug("refresh daily indicator success, refreshed {}", count);
                        applicationContext.publishEvent(DailyIndicatorChangeEvent.builder()
                                .build());
                    }, e -> {
                        logger.error("failed to refresh daily indicator", e);
                    });
            sink.next("<refreshDailyIndicator> job running...");
            sink.complete();
        });
    }
}
