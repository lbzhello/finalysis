package xyz.liujin.finalysis.extractor.manager;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.ApplicationContext;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.base.entity.Stock;
import xyz.liujin.finalysis.base.event.KLineChangeEvent;
import xyz.liujin.finalysis.base.event.StockChangeEvent;
import xyz.liujin.finalysis.base.schedule.TaskPool;
import xyz.liujin.finalysis.base.service.KLineService;
import xyz.liujin.finalysis.base.service.StockService;
import xyz.liujin.finalysis.base.util.DateUtils;
import xyz.liujin.finalysis.extractor.StockExtractor;

import java.time.LocalDate;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * 股票数据提取控制器
 */
@Service
public class ExtractorManager {
    private static Logger logger = LoggerFactory.getLogger(ExtractorManager.class);

    @Autowired
    @Qualifier(StockExtractor.TUSHARE)
    private StockExtractor stockExtractor;

    @Autowired
    private StockService stockService;

    @Autowired
    private KLineService kLineService;

    @Autowired
    private ApplicationContext applicationContext;

    /**
     * 更新股票信息
     * @return
     */
    public Flux<String> refreshStock() {
        return Flux.create(sink -> {
            logger.debug("start crawlStock {}", stockExtractor.getClass());

            sink.next("start to crawl stock\n");

            stockExtractor.crawlStock()
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
                    .subscribe(codes -> {
                        logger.debug("refresh stock success, add {}", codes.size());
                        applicationContext.publishEvent(StockChangeEvent.builder()
                                .addCodes(codes)
                                .build());
                    }, e -> logger.error("failed to crawlStock", e));

            sink.next("job running...\n");
            sink.complete();
        });
    }

    /**
     * 爬取 K 线入库，更新股票信息
     * @param start 爬取开始日期，默认当天
     * @param end 爬取结束日期，默认当天
     * @param stockCodes 需要爬取的股票列表，默认所有
     * @return
     */
    public Flux<String> refreshKLine(@Nullable LocalDate start, @Nullable LocalDate end, @Nullable List<String> stockCodes) {
        return Flux.create(sink -> {
            logger.debug("start refreshKLine. class: {}", stockExtractor.getClass());

            // yyyy-MM-dd
            String startDate = Optional.ofNullable(start).map(DateUtils::formatDate).orElseGet(() -> {
                // 需要更新的日期，数据库最新
                LocalDate latestDate = Optional.ofNullable(kLineService.getNextDate()).orElse(LocalDate.now());
                return DateUtils.formatDate(latestDate);
            });
            // 默认当前日期
            String endDate = Optional.ofNullable(end).map(DateUtils::formatDate).orElse(DateUtils.formatDate(OffsetDateTime.now()));
            // 股票代码，默认所有股票
            List<String> codes = Optional.ofNullable(stockCodes).orElse(stockService.list().stream()
                    .map(Stock::getStockCode)
                    .collect(Collectors.toList()));

            sink.next("start to crawl k line\n");
            logger.debug("start to crawl k line [startDate:{}, endDate:{}]", startDate, endDate);

            stockExtractor.crawlKLine(startDate, endDate, codes)
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
                                .start(DateUtils.parseDate(startDate))
                                .end(DateUtils.parseDate(endDate))
                                .codes(codes)
                                .build());
                        }, e -> logger.error("failed to crawlKLine", e));

            sink.next("job running...\n");
            sink.complete();
        });
    }
}
