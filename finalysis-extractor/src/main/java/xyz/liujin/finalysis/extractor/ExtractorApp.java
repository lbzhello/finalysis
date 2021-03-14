package xyz.liujin.finalysis.extractor;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import xyz.liujin.finalysis.base.service.KLineService;
import xyz.liujin.finalysis.extractor.manager.ExtractManager;

import java.time.LocalDate;

@Configuration
public class ExtractorApp {
    private Logger logger = LoggerFactory.getLogger(ExtractorApp.class);

    @Autowired
    private KLineService kLineService;

    @Autowired
    private ExtractManager extractManager;

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
        extractManager.refreshStock();
    }

    /**
     * 根据 KLine 表最新日期爬取最新的数据
     * 每天 17:20 爬取 k 线信息
     */
    @Scheduled(cron = "0 20 17 * * ?")
    public void refreshKLineDaily() {
        LocalDate now = LocalDate.now();

        logger.debug("refresh k line daily {}", now);

        extractManager.refreshKLine(now, now, null);
    }


}
