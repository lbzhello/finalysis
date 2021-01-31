package xyz.liujin.finalysis.spider.handler;

import cn.hutool.core.text.CharSequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.common.entity.Stock;
import xyz.liujin.finalysis.common.util.DateUtils;
import xyz.liujin.finalysis.spider.crawler.StockCrawler;
import xyz.liujin.finalysis.spider.service.KLineService;
import xyz.liujin.finalysis.spider.service.StockService;

import java.time.OffsetDateTime;

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

    /**
     * 爬取股票数据并入库
     * @return
     */
    public Mono<ServerResponse> crawlStock(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(Flux.create(sink -> {
                    sink.next("start to crawl stock\n");

                    logger.debug("start crawlStock {}", stockCrawler.getClass());
                    stockCrawler.crawlStock()
                            .subscribe(stock -> {
                                stockService.saveOrUpdate(stock);
                            }, e -> logger.error("failed to crawlStock", e));

                    sink.next("job running...\n");
                    sink.complete();
                }), String.class);

    }

    /**
     * 提供日期，则按日期爬取 K 线数据
     * 爬取 k 线数据并入库
     * @return
     */
    public Mono<ServerResponse> crawlKLine(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(Flux.create(sink -> {
                    sink.next("start to crawl k line\n");
                    // yyyy-MM-dd
                    String startDate = serverRequest.queryParam("startDate").orElse(DateUtils.formatDate(OffsetDateTime.now()));
                    // 默认当前日期
                    String endDate = serverRequest.queryParam("endDate").orElse(DateUtils.formatDate(OffsetDateTime.now()));

                    // 股票代码，例如 000001,000002
                    logger.debug("start crawlKLine class: {}", stockCrawler.getClass());

                    String[] codes;
                    String codeStr = serverRequest.queryParam("codes").orElse("");
                    // 提供参数，根据参数查询
                    if (CharSequenceUtil.isNotBlank(codeStr)) {
                        codes = codeStr.split(",");
                    } else {
                        // 未提供股票代码，爬取所有
                        codes = stockService.list().stream()
                                .map(Stock::getStockCode)
                                .toArray(i -> new String[i]);
                    }

                    stockCrawler.crawlKLine(startDate, endDate, codes)
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe(kLine -> kLineService.saveOrUpdate(kLine),
                                    e -> logger.error("failed to crawlKLine", e));


                    sink.next("job running...\n");
                    sink.complete();
                }), String.class);

    }
}
