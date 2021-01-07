package xyz.liujin.finalysis.spider.tushare;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.annotation.Nullable;
import xyz.liujin.finalysis.spider.crawler.StockCrawler;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.entity.Stock;
import xyz.liujin.finalysis.spider.service.StockService;

import java.io.IOException;
import java.util.List;

/**
 * Tushare K 线数据爬取
 */
@Component
public class TushareCrawler implements StockCrawler {
    private static Logger logger = LoggerFactory.getLogger(TushareCrawler.class);

    @Autowired
    private StockService stockService;

    public static void main(String[] args) {

    }

    @Override
    public Flux<Stock> crawlStock() {
        return Flux.just();
    }

    @Override
    public Flux<KLineDto> crawlKLine(String startDate, String endDate) {
        // 爬取所有股票 K 线
        return Tushare.Daily.builder()
//                .ts_code("")
                .start_date(yyyyMMdd(startDate))
                .end_date(yyyyMMdd(endDate))
                .build()
                .req()
                .flatMap(response -> {
                    try {
                        String body = response.body().string();
                        TushareResp tushareResp = JSONUtil.toBean(body, TushareResp.class);
                        return toKLineDto(tushareResp);
                    } catch (IOException e) {
                        logger.error("failed to call tushare.daily");
                    }
                    return Flux.just();
                });
    }

    // yyyy-MM-dd -> yyyyMMdd
    private String yyyyMMdd(@Nullable String dateStr) {
        return CharSequenceUtil.removeAny(dateStr, "-");
    }

    /**
     * 日线请求转成 k 线数据
     * ts_code trade_date open high low close pre_close change pct_chg vol amount
     * @return
     */
    private Flux<KLineDto> toKLineDto(TushareResp resp) {
        List<String> fields = resp.getData().getFields();
        return Flux.fromIterable(resp.getData().getItems())
                .map(item -> {
                    KLineDto kLineDto = new KLineDto();
                    // 循环设置每个字段
                    for (int i = 0; i < fields.size(); i++) {
                        String field = fields.get(i); // 字段名
                        String value = item.get(i); // 字段名对应的字段值
                        switch (field) {
                            case "ts_code" -> kLineDto.setStockCode(TushareUtil.removeSuffix(value));
                            case "trade_date" -> kLineDto.setDateTime(value);
                            case "open" -> kLineDto.setOpen(value);
                            case "high" -> kLineDto.setHigh(value);
                            case "low" -> kLineDto.setLow(value);
                            case "close" -> kLineDto.setClose(value);
                            case "pre_close" -> System.out.println();
                            case "change" -> kLineDto.setChange(value);
                            case "pct_chg" -> kLineDto.setPctChange(value);
                            case "vol" -> kLineDto.setVolume(value);
                            case "amount" -> kLineDto.setAmount(value);
                        }
                    }
                    return kLineDto;
                });
    }

}
