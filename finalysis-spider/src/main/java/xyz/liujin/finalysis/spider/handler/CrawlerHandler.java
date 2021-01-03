package xyz.liujin.finalysis.spider.handler;

import com.baomidou.mybatisplus.extension.conditions.update.UpdateChainWrapper;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.spider.crawler.StockCrawler;
import xyz.liujin.finalysis.spider.entity.KLine;
import xyz.liujin.finalysis.spider.service.KLineService;
import xyz.liujin.finalysis.spider.service.StockService;

import java.util.List;

@Component
public class CrawlerHandler {
    public static final Logger logger = LoggerFactory.getLogger(CrawlerHandler.class);

    @Autowired
    private List<StockCrawler> stockCrawlers;

    @Autowired
    private StockService stockService;

    @Autowired
    private KLineService kLineService;

    /**
     * 爬取股票数据并入库
     * @return
     */
    public Mono<ServerResponse> crawStock(ServerRequest serverRequest) {
        stockCrawlers.forEach(stockCrawler -> {
            logger.debug("start crawlStock {}", stockCrawler.getClass());
            stockCrawler.crawlStock()
                    .subscribe(stock -> {
                        stockService.saveOrUpdate(stock);
                    }, e -> logger.error("failed to crawlStock", e));
        });
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("running..."));

    }

    /**
     * 爬取 k 线数据并入库
     * @return
     */
    public Mono<ServerResponse> crawKLine(ServerRequest serverRequest) {
        stockCrawlers.forEach(stockCrawler -> {
            logger.debug("start crawlKLine class: {}", stockCrawler.getClass());
            stockCrawler.crawlKLine()
                    .subscribe(kLine -> {
                        kLineService.update(kLine);
                    }, e -> logger.error("failed to crawlKLine", e));
        });
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("running..."));

    }

    /**
     * 爬取指定股票 k 线，并入库
     * @return
     */
    public Mono<ServerResponse> crawKLineByCode(ServerRequest serverRequest) {
        String stockCode = serverRequest.pathVariable("stockCode");
        stockCrawlers.forEach(stockCrawler -> {
            logger.debug("start crawlKLine code: {} class: {}", stockCode, stockCrawler.getClass());
            stockCrawler.crawlKLine(stockCode)
                    .subscribe(kLine -> {
                        kLineService.update(kLine);
                    }, e -> logger.error("failed to crawlKLine", e));
        });
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("running..."));

    }
}
