package xyz.liujin.finalysis.spider.crawler;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.common.constant.BoardEnum;
import xyz.liujin.finalysis.common.util.DateUtil;
import xyz.liujin.finalysis.spider.constant.HtmlConst;
import xyz.liujin.finalysis.spider.constant.StockConst;
import xyz.liujin.finalysis.spider.constant.SzseConst;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.entity.KLine;
import xyz.liujin.finalysis.spider.entity.Stock;
import xyz.liujin.finalysis.spider.service.StockService;
import xyz.liujin.finalysis.spider.util.HttpUtils;

import java.math.BigDecimal;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.function.Consumer;

/**
 * 深圳证券交易所数据爬取
 */
@Component
public class SzseCrawler implements StockCrawler {
    private static Logger logger = LoggerFactory.getLogger(SzseCrawler.class);

    @Autowired
    private StockService stockService;

    public static void main(String[] args) {
        SzseCrawler szseCrawler = new SzseCrawler();
        szseCrawler.crawlKLine()
                .subscribe(kLine -> {
                    System.out.println(kLine);
                });

    }

    @Override
    public Flux<KLine> crawlKLine() {
//        String stockCode = "002594";
        // 爬取所有股票 K 线
        return Flux.create((Consumer<FluxSink<Stock>>) fluxSink -> {
            try {
                stockService.lambdaQuery().list().forEach(stock -> {
                    fluxSink.next(stock);
                });
            } catch (Exception e) {
                logger.error("failed to query stock", e);
                fluxSink.error(e);
            }
            fluxSink.complete();
        })
                .map(Stock::getStockCode)
                // 异步
                .flatMap(stockCode -> crawlKLine(stockCode)
                        .subscribeOn(Schedulers.boundedElastic()));
    }

    /**
     * 爬取 stockCode 股票日 K 数据
     * @param stockCode
     * @return
     */
    public Flux<KLine> crawlKLine(String stockCode) {
        logger.debug("crawlKLine {}", stockCode);
        return HttpUtils.get(SzseConst.GET_HISTORY_DATA_OF_DAY.formatted(stockCode)).flux()
                .map(response -> {
                    try {
                        String bodyStr = response.body().string();
                        JSONObject body = JSONUtil.parseObj(bodyStr);
                        JSONObject data = body.getJSONObject(SzseConst.DATA);
                        JSONArray picupdata = data.getJSONArray("picupdata");
                        return picupdata;
                    } catch (Exception e) {
                        logger.error("failed to get kline: " + stockCode, e);
                    }
                    return new JSONArray();
                })
                // [[date, open, close, low, high, inc, inc_rate, volume, amount]]
                .flatMap(picupdata -> Flux.fromIterable(picupdata.toList(JSONArray.class)))
                // ["2020-12-31", "188.86", "194.30", "184.26", "195.83", "8.30", "4.46", 505119, 9665582056]
                .map(arr -> {
                    String date = arr.getStr(0, "");
                    BigDecimal open = arr.getBigDecimal(1, BigDecimal.ZERO);
                    BigDecimal close = arr.getBigDecimal(2, BigDecimal.ZERO);
                    BigDecimal low = arr.getBigDecimal(3, BigDecimal.ZERO);
                    BigDecimal high = arr.getBigDecimal(4, BigDecimal.ZERO);
                    BigDecimal inc = arr.getBigDecimal(5, BigDecimal.ZERO);
                    BigDecimal incRate = arr.getBigDecimal(6, BigDecimal.ZERO);
                    Integer volume = arr.getInt(7, 0);
                    BigDecimal amount = arr.getBigDecimal(8, BigDecimal.ZERO);
                    return KLine.builder()
                            .stockCode(stockCode)
                            .dateTime(DateUtil.parseDate(date))
                            .open(open)
                            .close(close)
                            .low(low)
                            .high(high)
                            .inc(inc)
                            .incRate(incRate)
                            .volume(volume)
                            .amount(amount)
                            .build();
                });
    }

    /**
     * 爬取深交所所有股票数据
     * @return
     */
    @Override
    public Flux<Stock> crawlStock() {
        String today = DateUtil.formatDate(OffsetDateTime.now());
        String day1231 = "2020-12-31";
        return HttpUtils.get(SzseConst.GET_REPORT.formatted(day1231, day1231, 1))
                .flux()
                // 解析响应数据
                .map(response -> {
                    try {
                        String body = response.body().string();
                        JSONArray jsonArray = JSONUtil.parseArray(body);
                        // 单个元素的列表
                        JSONObject jsonObject = jsonArray.get(0, JSONObject.class);
                        JSONObject metadata = jsonObject.getJSONObject(SzseConst.METADATA);
                        Integer pagecount = metadata.getInt("pagecount"); // 总页数
                        return pagecount;
                    } catch (Exception e) {
                        logger.error("failed to get total page", e);
                        e.printStackTrace();
                    }
                    return 0;
                })
                // 每页查询一次
                .flatMap(pageCount -> Flux.create((Consumer<FluxSink<Integer>>) fluxSink -> {
                    for (int i = 1; i < pageCount; i++) {
                        fluxSink.next(i);
                    }
                    fluxSink.complete();
                }))
                // 异步获取每页数据
                .flatMap(page -> {
                    Flux<Response> responseFlux = HttpUtils.get(SzseConst.GET_REPORT.formatted(day1231, day1231, page))
                            .flux()
                            .subscribeOn(Schedulers.boundedElastic());
                    return retrieveStockFromResponse(responseFlux);
                });
    }

    /**
     * 从请求解析数据
     * @param responseFlux
     * @return
     */
    private Flux<Stock> retrieveStockFromResponse(Flux<Response> responseFlux) {
        return responseFlux.map(response -> {
            try {
                return response.body().string();
            } catch (Exception e) {
                logger.error("failed to retrieve response data", e);
                throw new RuntimeException("szse failed to get response data");
            }
        })
                // 解析响应数据
                .map(body -> {
                    JSONArray jsonArray = JSONUtil.parseArray(body);
                    // 单个元素的列表
                    JSONObject jsonObject = jsonArray.get(0, JSONObject.class);
                    JSONArray dataArr = jsonObject.getJSONArray(SzseConst.DATA);
                    return dataArr;
                })
                .flatMap(dataArr -> Flux.fromIterable(dataArr.toList(JSONObject.class)))
                .map(jsonObject -> {
                    // 股票代码
                    String zqdm = jsonObject.getStr("zqdm", "");
                    // 股票名称
                    String zqjc = CharSequenceUtil.removeAny(jsonObject.getStr("zqjc"),
                            // 去除 ST *ST &nbsp; 等字符
                            StockConst.ST, StockConst.STAR_ST, HtmlConst.SPACE);
                    // 交易板块
                    Integer board = BoardEnum.getBoardByCode(zqdm);
                    return Stock.builder().stockCode(zqdm).stockName(zqjc).board(board).build();
                });
    }

}
