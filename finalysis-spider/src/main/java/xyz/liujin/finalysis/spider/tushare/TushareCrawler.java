package xyz.liujin.finalysis.spider.tushare;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.util.annotation.Nullable;
import xyz.liujin.finalysis.common.constant.BoardEnum;
import xyz.liujin.finalysis.common.util.JsonExtractor;
import xyz.liujin.finalysis.spider.constant.StockConst;
import xyz.liujin.finalysis.spider.crawler.StockCrawler;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.dto.StockDto;
import xyz.liujin.finalysis.spider.entity.Stock;
import xyz.liujin.finalysis.spider.service.StockService;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * Tushare K 线数据爬取
 */
@Component
public class TushareCrawler implements StockCrawler {
    private static Logger logger = LoggerFactory.getLogger(TushareCrawler.class);

    @Autowired
    private StockService stockService;

    public static void main(String[] args) throws InterruptedException {
        AtomicInteger count = new AtomicInteger(0);
        TushareCrawler tushareCrawler = new TushareCrawler();
        tushareCrawler.crawlStock()
                .doOnNext(it -> count.addAndGet(1))
                .subscribe(it -> {
                    System.out.println(it);
                });
        Thread.sleep(20*1000);
        System.out.println(count.get());
    }

    @Override
    public Flux<Stock> crawlStock() {
        return Tushare.StockBasic.builder()
                .build()
                .req()
                .flatMap(response -> {
                    try {
                        String bodyStr = response.body().string();
                        TushareResp tushareResp = JSONUtil.toBean(bodyStr, TushareResp.class);
                        TushareRespData data = tushareResp.getData();
                        Map<String, Object> mapper = new HashMap<>();
                        mapper.put("stockCode", "/symbol");
                        mapper.put("stockName", "/name");
                        mapper.put("board", "");
                        mapper.put("stat", ""); // L 上市， D 退市， P 暂停，
                        mapper.put("listingDate", "/list_date");

                        return JsonExtractor.csvMap(Flux.fromIterable(data.getFields()),
                                Flux.fromIterable(data.getItems()).map(item -> Flux.fromIterable(item)), mapper)
                                .map(it -> JSONUtil.toBean(JSONUtil.parseObj(it), StockDto.class))
                                .map(stockDto -> toStock(stockDto));
                    } catch (Exception e) {
                        logger.error("crawlStock failed", e);
                    }
                    return Flux.just();
                });
    }

    @Override
    public Flux<KLineDto> crawlKLine(String startDate, String endDate, String... codes) {
        // 爬取所有股票 K 线
        return Tushare.Daily.builder()
                .ts_code(formatCodes(codes))
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

    private Stock toStock(StockDto stockDto) {
        // 股票状态
        String statStr = stockDto.getStat();
        int stat = 0;
        if (Objects.equals(statStr, "L")) {
            stat = StockConst.NORMAL;
        } else if (Objects.equals(statStr, "D")) {
            stat = StockConst.DE_LISTING;
        } else if (Objects.equals(statStr, "P")) {
            stat = StockConst.PAUSE_LISTING;
        }
        String dateStr = stockDto.getListingDate();
        LocalDate offsetDate = LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd"));

        return Stock.builder()
                .stockCode(parseCode(stockDto.getStockCode()))
                .stockName(stockDto.getStockName())
                .board(BoardEnum.getBoardByCode(stockDto.getStockCode()))
                .stat(stat)
                .listingDate(offsetDate)
                .build();
    }

    // 000001.SZ -> 000001
    private String parseCode(String code) {
        return CharSequenceUtil.removeAny(code, StockConst.POINT, StockConst.SH, StockConst.SZ);
    }

    // [000001, 600001] -> 000001.SZ,600001.SH
    private String formatCodes(String[] codes) {
        return Optional.ofNullable(codes)
                .stream()
                .flatMap(Arrays::stream)
                .map(TushareUtil::appendSuffix)
                .collect(Collectors.joining(","));

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
                            case "trade_date" -> kLineDto.setDateTime(formatDate(value));
                            case "open" -> kLineDto.setOpen(value);
                            case "high" -> kLineDto.setHigh(value);
                            case "low" -> kLineDto.setLow(value);
                            case "close" -> kLineDto.setClose(value);
                            case "pre_close" -> System.out.println();
                            case "change" -> kLineDto.setChange(value);
                            case "pct_chg" -> kLineDto.setPctChange(value);
                            case "vol" -> kLineDto.setVolume(getVolShares(value));
                            case "amount" -> kLineDto.setAmount(getAmountYuan(value));
                        }
                    }
                    return kLineDto;
                });
    }

    // yyyyMMdd -> yyyy-MM-dd
    private String formatDate(String dateStr) {
        return LocalDate.parse(dateStr, DateTimeFormatter.ofPattern("yyyyMMdd")).format(DateTimeFormatter.ISO_DATE);
    }

    // 成交量 tushare 单位是手, 改为股
    private static String getVolShares(String vol) {
        if (CharSequenceUtil.isBlank(vol)) {
            return "0";
        }
        return String.valueOf(new BigDecimal(vol).multiply(BigDecimal.valueOf(100)).longValue());
    }

    // 成交额 tushare 单位：千元，改为 元
    private static String getAmountYuan(String amount) {
        if (CharSequenceUtil.isBlank(amount)) {
            return "0";
        }
        return new BigDecimal(amount).multiply(BigDecimal.valueOf(1000)).toString();
    }

}
