package xyz.liujin.finalysis.spider.crawler;

import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.spider.dto.KLineDto;
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
     * @param startDate yyyy-MM-dd 开始时间，包含，为空则不过滤
     * @param endDate   yyyy-MM-dd 结束时间，包含，为空则不过滤
     * @return
     */
    Flux<KLineDto> crawlKLine(String startDate, String endDate);

}
