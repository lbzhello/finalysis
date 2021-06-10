package xyz.liujin.finalysis.extractor.tushare.manager;

import cn.hutool.core.collection.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;
import xyz.liujin.finalysis.analysis.dto.HeavenVolRatioQo;
import xyz.liujin.finalysis.analysis.dto.RecommendQo;
import xyz.liujin.finalysis.analysis.service.AnalysisService;
import xyz.liujin.finalysis.base.executor.TaskPool;
import xyz.liujin.finalysis.base.page.PageQo;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.base.util.SpringUtils;
import xyz.liujin.finalysis.daily.converter.KLineConverter;
import xyz.liujin.finalysis.daily.event.DailyIndicatorChangeEvent;
import xyz.liujin.finalysis.daily.event.KLineChangeEvent;
import xyz.liujin.finalysis.daily.service.DailyIndicatorService;
import xyz.liujin.finalysis.daily.service.KLineService;
import xyz.liujin.finalysis.extractor.tushare.TushareDailyIndicatorExtractor;
import xyz.liujin.finalysis.extractor.tushare.TushareKLineExtractor;
import xyz.liujin.finalysis.extractor.tushare.TushareStockExtractor;
import xyz.liujin.finalysis.stock.entity.Stock;
import xyz.liujin.finalysis.stock.event.StockChangeEvent;
import xyz.liujin.finalysis.stock.service.StockService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

/**
 * 股票数据提取控制器
 */
@Service
public class TushareManager {
    private static Logger logger = LoggerFactory.getLogger(TushareManager.class);

    @Autowired
    private TushareStockExtractor tushareStockExtractor;

    @Autowired
    private TushareKLineExtractor tushareKLineExtractor;

    @Autowired
    private StockService stockService;

    @Autowired
    private KLineService kLineService;

    @Autowired
    private ApplicationContext applicationContext;

    @Autowired
    private TushareDailyIndicatorExtractor tushareDailyIndicatorExtractor;

    @Autowired
    private DailyIndicatorService dailyIndicatorService;

    @Autowired
    private AnalysisService analysisService;

    /**
     * 自动更新股票数据
     * 1. 更新股票数据
     * 2. 更新 K 线数据
     * 3. 更新每日指标数据
     * 4. 推荐股票自动入库
     * @param start 开始日期；默认数据库最新
     * @param end   结束日期；默认当日
     * @param codes 股票代码列表；默认所有
     * @return
     */
    public Flux<String> refreshAll(@Nullable LocalDate start, @Nullable LocalDate end, @Nullable List<String> codes) {
        logger.debug("start to refresh all tasks...");

        refreshStock()
                // 先更新股票数据，再更新 K 线和日指标表
                .flatMap(stockCount -> Flux.merge(Flux.just(stockCount),
                        refreshKLine(start, end, codes),
                        refreshDailyIndicator(start, end, codes)))
                // 统计任务个数
                .map(it -> 1)
                .reduce(Integer::sum)
                .flux()
                .subscribeOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                .subscribe(it -> {
                    logger.debug("refresh all tasks success, tasks {}", it);
                    // 完成后自动计算推荐股票
                    LocalDate date = ObjectUtils.firstNonNull(dailyIndicatorService.getLatestDate(), LocalDate.now());
                    analysisService.recommend(RecommendQo.builder()
                            .store(true)
                            .date(date)
                            .heavenVolRatio(HeavenVolRatioQo.builder()
                                    .days(6)
                                    .minVolRatio(BigDecimal.valueOf(2))
                                    .build())
                            .page(PageQo.builder()
                                    .limit(1000)
                                    .orderBy("amount desc")
                                    .build())
                            .build())
                            .subscribe();
                }, e -> logger.error("failed to refresh all", e));

        return Flux.just("start to refresh all tasks...");

    }

    /**
     * 更新股票信息
     * @return
     */
    public Flux<Integer> refreshStock() {
        logger.debug("start extract stock {}", tushareKLineExtractor.getClass());

        List<String> addCodes = new ArrayList<>();
        return tushareStockExtractor.extractStock()
                // 获取新增的股票
                .map(stock -> {
                    Stock exist = stockService.getById(stock.getStockCode());
                    // 存在更新
                    if (Objects.nonNull(exist)) {
                        stockService.updateById(stock);
                    } else {
                        stockService.save(stock);
                        addCodes.add(stock.getStockCode());
                    }
                    return 1;
                })
                .reduce(Integer::sum)
                .flux()
                // 发布股票变更事件
                .doOnNext(count -> {
                    logger.debug("refresh stock success, count {}", count);
                    SpringUtils.getApplicationContext().publishEvent(StockChangeEvent.builder()
                            .addCodes(addCodes)
                            .build());
                });
    }

    /**
     * 爬取 K 线入库，更新股票信息
     * @param start 爬取开始日期，默认数据库最新数据
     * @param end 爬取结束日期，默认当天
     * @param stockCodes 需要爬取的股票列表，默认所有
     * @return
     */
    public Flux<Integer> refreshKLine(@Nullable LocalDate start, @Nullable LocalDate end, @Nullable List<String> stockCodes) {
        return Flux.create((Consumer<FluxSink<Tuple3<LocalDate, LocalDate, List<String>>>>) sink -> {
            logger.debug("start refreshKLine. class: {}", tushareKLineExtractor.getClass());

            // 需要更新的日期，数据库最新
            LocalDate startDate = ObjectUtils.firstNonNull(start, kLineService.getNextDate(), LocalDate.now());
            // 默认当前日期
            LocalDate endDate = Optional.ofNullable(end).orElse(LocalDate.now());
            // 股票代码，默认所有股票
            List<String> codes = CollectionUtil.isNotEmpty(stockCodes) ? stockCodes : stockService.list().stream()
                            .map(Stock::getStockCode)
                            .collect(Collectors.toList());

            sink.next(Tuples.of(startDate, endDate, codes));
            sink.complete();
        })
                .flatMap(tuple -> {
                    LocalDate startDate = tuple.getT1();
                    LocalDate endDate = tuple.getT2();
                    List<String> codes = tuple.getT3();

                    logger.debug("start to extract k line [startDate:{}, endDate:{}]", startDate, endDate);

                    return tushareKLineExtractor.extractKLine(startDate, endDate, codes)
//                            .subscribeOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                            .parallel(TaskPool.availableProcessors())
                            .runOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                            // 入库
                            .map(KLineConverter::toKLine)
                            .map(kLine -> {
                                kLineService.insertOrUpdate(kLine);
                                return 1;
                            })
                            // 统计更新条目；到这里任务已经执行完毕
                            .reduce(Integer::sum)
                            // 发布 k 线变更事件
                            .doOnNext(count -> {
                                logger.debug("refresh k line success, refreshed {}", count);
                                SpringUtils.getApplicationContext().publishEvent(KLineChangeEvent.builder()
                                        .start(startDate)
                                        .end(endDate)
                                        .codes(codes)
                                        .build());
                            });
                });
    }

    /**
     * 更新股票日指标数据
     * @param start 开始日期；默认从数据库获取最新日期的下一个日期
     * @param end   结束日期；默认当天
     * @param codes 股票列表；默认所有
     * @return
     */
    public Flux<Integer> refreshDailyIndicator(@Nullable LocalDate start, @Nullable LocalDate end, @Nullable List<String> codes) {
        return Flux.create((Consumer<FluxSink<Tuple3<LocalDate, LocalDate, List<String>>>>) sink -> {
            logger.debug("start refresh daily indicator {}", tushareKLineExtractor.getClass());

            // 默认从数据库获取最新日期的下一个日期
            LocalDate startDate = ObjectUtils.firstNonNull(start, dailyIndicatorService.getNextDate(), LocalDate.now());
            // 默认当天
            LocalDate endDate = Optional.ofNullable(end).orElse(LocalDate.now());
            // 默认数据库所有股票
            List<String> stockCodes = CollectionUtil.isNotEmpty(codes) ? codes : stockService.list().stream()
                    .map(Stock::getStockCode)
                    .collect(Collectors.toList());

            sink.next(Tuples.of(startDate, endDate, stockCodes));
            sink.complete();
        })
                .flatMap(tuple -> {
                    LocalDate startDate = tuple.getT1();
                    LocalDate endDate = tuple.getT2();
                    List<String> stockCodes = tuple.getT3();

                    return tushareDailyIndicatorExtractor.extractDailyIndicator(startDate, endDate, stockCodes)
//                            .subscribeOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                            .parallel(TaskPool.availableProcessors())
                            .runOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                            // 入库
                            .map(dailyIndicator -> {
                                dailyIndicatorService.saveByCodeDate(dailyIndicator);
                                return 1;
                            })
                            .reduce(Integer::sum)
                            // 发布股票日指标变更事件
                            .doOnNext(count -> {
                                logger.debug("refresh daily indicator success, refreshed {}", count);
                                SpringUtils.getApplicationContext().publishEvent(DailyIndicatorChangeEvent.builder()
                                        .build());
                            });
                });
    }
}
