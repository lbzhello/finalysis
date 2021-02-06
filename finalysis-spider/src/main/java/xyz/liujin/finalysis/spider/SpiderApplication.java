package xyz.liujin.finalysis.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.common.entity.KLine;
import xyz.liujin.finalysis.common.entity.Stock;
import xyz.liujin.finalysis.common.service.KLineService;
import xyz.liujin.finalysis.common.service.StockService;
import xyz.liujin.finalysis.common.util.DateUtils;
import xyz.liujin.finalysis.spider.crawler.StockCrawler;

import java.time.LocalDate;

@Configuration
public class SpiderApplication {
    private Logger logger = LoggerFactory.getLogger(SpiderApplication.class);

    @Autowired
    private StockService stockService;

    @Autowired
    private KLineService kLineService;

    @Autowired
    @Qualifier("tushareCrawler")
    private StockCrawler stockCrawler;



    @Bean
    public ApplicationRunner onSpiderStartUp() {
        return args -> {
            logger.debug("onSpiderStartUp running...");
        };
    }

    /**
     * 每日爬取股票信息爬取股票
     * 每天 00:00 更新股票信息
     */
    @Scheduled(cron = "0 0 0 * * ?")
    public void refreshStockDaily() {
        logger.debug("start refreshStockDaily {}", stockCrawler.getClass());
        stockCrawler.crawlStock()
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(stock -> {
                    stockService.saveOrUpdate(stock);
                }, e -> logger.error("failed to refreshStockDaily", e));
    }

    /**
     * 根据 KLine 表最新日期爬取最新的数据
     * 每天 17:20 爬取 k 线信息
     */
    @Scheduled(cron = "0 20 17 * * ?")
    public void refreshKLineDaily() {
        logger.debug("refresh k line daily {}", LocalDate.now());
        // 获取 k_line 数据库最新日期，默认当天
        LocalDate curDate = kLineService.lambdaQuery()
                .orderByDesc(KLine::getDate)
                .oneOpt()
                .map(KLine::getDate)
                .orElse(LocalDate.now());
        // 需要爬取的日期
        String start = DateUtils.formatDate(curDate.plusDays(1));
        String end = DateUtils.formatDate(LocalDate.now());

        // 未提供股票代码，爬取所有
        String[] codes = stockService.list().stream()
                .map(Stock::getStockCode)
                .toArray(i -> new String[i]);

        // 爬取股票信息
        stockCrawler.crawlKLine(start, end, codes)
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(it -> kLineService.saveOrUpdate(it),
                        e -> logger.error("failed to refresh k line daily", e));
    }


}
