package xyz.liujin.finalysis.extractor.tushare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import xyz.liujin.finalysis.daily.service.KLineService;
import xyz.liujin.finalysis.extractor.tushare.manager.TushareManager;

import java.time.LocalDateTime;

@Configuration
public class TushareApp {
    private Logger logger = LoggerFactory.getLogger(TushareApp.class);

    @Autowired
    private KLineService kLineService;

    @Autowired
    private TushareManager tushareManager;

    @Bean
    public ApplicationRunner onSpiderStartUp() {
        return args -> {
            logger.debug("onSpiderStartUp running...");
        };
    }

    /**
     * 根据 KLine 表最新日期爬取最新的数据
     * 每天 17:20 爬取 k 线信息
     */
    @Scheduled(cron = "0 20 17 * * ?")
    public void refreshDaily() {
        logger.debug("auto refresh data daily {}", LocalDateTime.now());

        tushareManager.refreshAll();
    }


}
