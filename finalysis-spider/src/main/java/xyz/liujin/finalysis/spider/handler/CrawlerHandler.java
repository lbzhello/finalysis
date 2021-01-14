package xyz.liujin.finalysis.spider.handler;

import cn.hutool.core.text.CharSequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.common.util.DateUtils;
import xyz.liujin.finalysis.spider.crawler.StockCrawler;
import xyz.liujin.finalysis.spider.entity.Stock;
import xyz.liujin.finalysis.spider.service.KLineService;
import xyz.liujin.finalysis.spider.service.StockService;

import java.time.Duration;
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
    public Mono<ServerResponse> crawStock(ServerRequest serverRequest) {
        logger.debug("start crawlStock {}", stockCrawler.getClass());
        stockCrawler.crawlStock()
                .subscribe(stock -> {
                    stockService.saveOrUpdate(stock);
                }, e -> logger.error("failed to crawlStock", e));
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("running..."));

    }

    /**
     * 提供日期，则按日期爬取 K 线数据
     * 爬取 k 线数据并入库
     * @return
     */
    public Mono<ServerResponse> crawlKLine(ServerRequest serverRequest) {
        // yyyy-MM-dd
        String startDate = serverRequest.queryParam("startDate").orElse(null);
        // 默认当前日期
        String endDate = serverRequest.queryParam("endDate").orElse(DateUtils.formatDate(OffsetDateTime.now()));

        // 股票代码，例如 000001,000002
        logger.debug("start crawlKLine class: {}", stockCrawler.getClass());

        Flux<String> codeFlux;
        String codeStr = serverRequest.queryParam("codes").orElse("");
        // 提供参数，根据参数查询
        if (CharSequenceUtil.isNotBlank(codeStr)) {
            String[] codes = codeStr.split(",");
            codeFlux = Flux.fromArray(codes);
        } else {
            // 未提供日期和股票代码，爬取所有
            codeFlux = Flux.fromIterable(stockService.list())
                    .map(Stock::getStockCode);
        }

        // 每分钟最多调用 500 次（每秒最多调用 8 次）
        codeFlux.delayElements(Duration.ofMillis(1000 / 8))
                .subscribe(code -> {
                    stockCrawler.crawlKLine(startDate, endDate, code)
                            .subscribeOn(Schedulers.boundedElastic())
                            .subscribe(kLine -> {
                                kLineService.saveOrUpdate(kLine);
                            }, e -> logger.error("failed to crawlKLine", e));
                });
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue("running..."));

    }

//    /**
//     * 爬取指定股票 k 线，并入库
//     * @return
//     */
//    public Mono<ServerResponse> crawKLineByCode(ServerRequest serverRequest) {
//        String stockCode = serverRequest.pathVariable("stockCode");
//        stockCrawlers.forEach(stockCrawler -> {
//            logger.debug("start crawlKLine code: {} class: {}", stockCode, stockCrawler.getClass());
//            stockCrawler.crawlKLine(stockCode)
//                    .subscribe(kLine -> {
//                        kLineService.saveOrUpdate(kLine);
//                    }, e -> logger.error("failed to crawlKLine", e));
//        });
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue("running..."));
//
//    }

//    /**
//     * 按日期爬取股票 k 线，未提供则爬取当日数据
//     * @return
//     */
//    public Mono<ServerResponse> crawKLineByDate(ServerRequest serverRequest) {
//        String date = serverRequest.pathVariable("date"); // yyyy-MM-dd
//        String dateStr = CharSequenceUtil.isNotBlank(date) ? date : DateUtil.formatDate(OffsetDateTime.now());
//        logger.debug("crawKLineByDate date: {}", dateStr);
//        stockCrawlers.forEach(stockCrawler -> {
//            logger.debug("start crawKLineByDate class: {}", stockCrawler.getClass());
//            stockCrawler.crawlKLine()
//                    .filter(kLineDto -> Objects.equals(kLineDto.getDateTime(), dateStr)) // 过滤指定日期的股票
//                    .subscribe(kLine -> {
//                        kLineService.saveOrUpdate(kLine);
//                    }, e -> logger.error("failed to crawKLineByDate", e));
//        });
//        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
//                .body(BodyInserters.fromValue("running..."));
//
//    }
}
