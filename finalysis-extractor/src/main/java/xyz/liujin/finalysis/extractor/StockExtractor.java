package xyz.liujin.finalysis.extractor;

import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.stock.entity.Stock;

/**
 * 股票数据爬虫接口
 */
public interface StockExtractor {
    String TUSHARE = "tushareExtractor";

    /**
     * 爬取股票数据，返回异步的股票数据流
     * 子类通过实现此方法，从不同的网站爬取股票数据
     * @return 异步流
     */
    Flux<Stock> extractStock();

}
