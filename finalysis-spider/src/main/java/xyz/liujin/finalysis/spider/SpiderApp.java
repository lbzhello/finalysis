package xyz.liujin.finalysis.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import xyz.liujin.finalysis.common.entity.KLine;
import xyz.liujin.finalysis.common.service.KLineService;
import xyz.liujin.finalysis.spider.manager.CrawlManager;

import java.time.LocalDate;

@Configuration
public class SpiderApp {
    private Logger logger = LoggerFactory.getLogger(SpiderApp.class);

    @Autowired
    private KLineService kLineService;

    @Autowired
    private CrawlManager crawlManager;

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
        logger.debug("start refresh stock daily {}", LocalDate.now());
        crawlManager.refreshStock();
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

        logger.debug("latest date in db k_line is {}", curDate);

        // 需要爬取的日期
        LocalDate start = curDate.plusDays(1);
        LocalDate end = LocalDate.now();

        crawlManager.refreshKLine(start, end, null);
    }


}
