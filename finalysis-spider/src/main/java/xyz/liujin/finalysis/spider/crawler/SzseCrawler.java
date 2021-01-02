package xyz.liujin.finalysis.spider.crawler;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import okhttp3.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.common.constant.BoardEnum;
import xyz.liujin.finalysis.common.util.DateUtil;
import xyz.liujin.finalysis.spider.constant.HtmlConst;
import xyz.liujin.finalysis.spider.constant.StockConst;
import xyz.liujin.finalysis.spider.constant.SzseConst;
import xyz.liujin.finalysis.spider.entity.Stock;
import xyz.liujin.finalysis.spider.util.HttpUtils;

import java.time.OffsetDateTime;
import java.util.function.Consumer;

@Component
public class SzseCrawler implements StockCrawler {
    private static Logger logger = LoggerFactory.getLogger(SzseCrawler.class);

    public static void main(String[] args) {
        SzseCrawler szseCrawler = new SzseCrawler();
        szseCrawler.crawlStock()
                .subscribe(stock -> {
                    System.out.println(stock);
                });
    }

    /**
     * 从请求解析数据
     * @param responseFlux
     * @return
     */
    private Flux<Stock> retrieveFromResponse(Flux<Response> responseFlux) {
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
                            // 去除 * ST &nbsp; 等字符
                            StockConst.STAR, StockConst.ST, HtmlConst.SPACE);
                    // 交易板块
                    Integer board = BoardEnum.getBoardByCode(zqdm);
                    return Stock.builder().stockCode(zqdm).stockName(zqjc).board(board).build();
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
                    return retrieveFromResponse(responseFlux);
                });
    }

}
