package xyz.liujin.finalysis.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import xyz.liujin.finalysis.spider.crawler.StockCrawler;
import xyz.liujin.finalysis.spider.crawler.SzseCrawler;
import xyz.liujin.finalysis.spider.service.StockService;

@Configuration
public class SpiderApplication {
    private Logger logger = LoggerFactory.getLogger(SpiderApplication.class);

    @Autowired
    @Qualifier("szseCrawler")
    private StockCrawler stockCrawler;

    @Autowired
    private StockService stockService;

    @Bean
    public ApplicationRunner onSpiderStartUp() {
        logger.debug("onSpiderStartUp running...");
        return args -> stockCrawler.crawlStock()
                .subscribe(stock -> {
                    stockService.saveOrUpdate(stock);
                });
    }
}
