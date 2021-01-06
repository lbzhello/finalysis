package xyz.liujin.finalysis.spider.tushare;

import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.spider.crawler.StockCrawler;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.entity.Stock;
import xyz.liujin.finalysis.spider.service.StockService;

import java.io.IOException;

public class TushareCrawler implements StockCrawler {
    private static Logger logger = LoggerFactory.getLogger(TushareCrawler.class);

    @Autowired
    private StockService stockService;

    public static void main(String[] args) {
        TushareCrawler tushareCrawler = new TushareCrawler();
        tushareCrawler.crawlKLine();
    }

    @Override
    public Flux<Stock> crawlStock() {
        return Flux.just();
    }

    @Override
    public Flux<KLineDto> crawlKLine() {
        // 爬取所有股票 K 线
        Tushare.Daily.builder()
//                .ts_code("")
                .trade_date("20210104")
                .build()
                .req()
                .subscribe(response -> {
                    try {
                        String body = response.body().string();
                        TushareResp tushareResp = JSONUtil.toBean(body, TushareResp.class);
                        System.out.println(tushareResp);
                    } catch (IOException e) {
                        logger.error("failed to call tushare.daily");
                    }
                });

        return null;
    }

    @Override
    public Flux<KLineDto> crawlKLine(String stockCode) {
        return null;
    }
}
