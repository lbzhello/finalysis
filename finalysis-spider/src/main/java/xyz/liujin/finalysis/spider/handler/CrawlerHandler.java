package xyz.liujin.finalysis.spider.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.common.service.KLineService;
import xyz.liujin.finalysis.common.service.StockService;
import xyz.liujin.finalysis.common.util.DateUtils;
import xyz.liujin.finalysis.spider.crawler.StockCrawler;
import xyz.liujin.finalysis.spider.manager.CrawlManager;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;

@Component
public class CrawlerHandler {
    public static final Logger logger = LoggerFactory.getLogger(CrawlerHandler.class);

    @Autowired
    @Qualifier("tushareCrawler")
    private StockCrawler stockCrawler;

    @Autowired
    private StockService stockService;

    @Autowired
    private KLineService kLineService;

    @Autowired
    private CrawlManager crawlManager;

    /**
     * 爬取股票数据并入库
     * @return
     */
    public Mono<ServerResponse> crawlStock(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(crawlManager.refreshStock(), String.class);

    }

    /**
     * 提供日期，则按日期爬取 K 线数据
     * 爬取 k 线数据并入库
     * @return
     */
    public Mono<ServerResponse> crawlKLine(ServerRequest serverRequest) {
        // yyyy-MM-dd
        LocalDate startDate = serverRequest.queryParam("startDate").map(DateUtils::parseDate).orElse(LocalDate.now());
        // 默认当前日期
        LocalDate endDate = serverRequest.queryParam("endDate").map(DateUtils::parseDate).orElse(LocalDate.now());
        // 股票列表
        List<String> codes = serverRequest.queryParam("codes").map(codeStr -> Arrays.asList(codeStr.split(","))).orElse(null);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(crawlManager.refreshKLine(startDate, endDate, codes), String.class);

    }
}
