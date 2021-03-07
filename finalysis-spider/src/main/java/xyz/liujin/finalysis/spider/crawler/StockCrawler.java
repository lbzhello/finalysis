package xyz.liujin.finalysis.spider.crawler;

import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.base.dto.KLineDto;
import xyz.liujin.finalysis.base.entity.Stock;

import java.util.List;

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
     * @param codes     股票代码列表，为空则表示所有，例如 [000001, 000002]
     * @param startDate yyyy-MM-dd 开始时间，包含，为空则不过滤; 例如 2021-01-01
     * @param endDate   yyyy-MM-dd 结束时间，包含，为空则不过滤; 例如 2021-01-02
     * @return
     */
    Flux<KLineDto> crawlKLine(@Nullable String startDate, @Nullable String endDate, @Nullable List<String> codes);

}
