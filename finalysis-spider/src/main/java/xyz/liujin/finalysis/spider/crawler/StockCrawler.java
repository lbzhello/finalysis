package xyz.liujin.finalysis.spider.crawler;

import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.entity.KLine;
import xyz.liujin.finalysis.spider.entity.Stock;

/**
 * 股票数据爬虫
 */
public interface StockCrawler {
    /**
     * 爬取股票数据，返回异步的股票数据流
     * 子类通过实现此方法，从不同的网站爬取股票数据
     * @return 异步流
     */
    Flux<Stock> crawlStock();

    /**
     * 爬取日 K 线数据
     * @return
     */
    Flux<KLineDto> crawlKLine();

    /**
     * 爬取指定股票的日 K 数据，爬取所有的太慢了
     * @param stockCode
     * @return
     */
    Flux<KLineDto> crawlKLine(String stockCode);
}
