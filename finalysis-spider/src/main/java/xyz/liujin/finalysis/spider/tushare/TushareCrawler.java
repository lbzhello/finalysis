package xyz.liujin.finalysis.spider.tushare;

import cn.hutool.core.text.CharSequenceUtil;
import cn.hutool.core.util.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Flux;
import reactor.util.function.Tuple3;
import reactor.util.function.Tuples;
import xyz.liujin.finalysis.common.constant.BoardEnum;
import xyz.liujin.finalysis.common.json.CsvMapper;
import xyz.liujin.finalysis.common.util.DateUtils;
import xyz.liujin.finalysis.common.util.SyncUtils;
import xyz.liujin.finalysis.spider.constant.StockConst;
import xyz.liujin.finalysis.spider.crawler.StockCrawler;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.dto.StockDto;
import xyz.liujin.finalysis.spider.entity.Stock;
import xyz.liujin.finalysis.spider.service.StockService;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.Arrays;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 * Tushare K 线数据爬取
 */
@Component
public class TushareCrawler implements StockCrawler {
    private static Logger logger = LoggerFactory.getLogger(TushareCrawler.class);

    @Autowired
    private StockService stockService;

    public static void main(String[] args) throws Exception {
        splitCodes("2000-01-20", "2021-01-26", new String[]{"a", "b", "c", "d", "e"})
                .subscribe(it -> {
                    System.out.println(it.getT1() + " " + it.getT2() + " " + it.getT3());
                });
    }

    @Override
    public Flux<Stock> crawlStock() {
        return Tushare.StockBasic.builder()
                .build()
                .req("symbol,name,list_status,list_date")
                .flatMap(response -> {
                    try {
                        String bodyStr = response.body().string();
                        // 获取映射文件
                        File file = ResourceUtils.getFile("classpath:tushare/stock_basic_2_stock.yml");
                        return CsvMapper.yamlFile(TushareResp.FIELDS_PATH, TushareResp.ITEMS_PATH, file)
                                .eval(bodyStr, StockDto.class)
                                .map(this::toStock);
                    } catch (Exception e) {
                        logger.error("crawlStock failed", e);
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
        LocalDate offsetDate = DateUtils.parseDate(dateStr, "yyyyMMdd");

        return Stock.builder()
                .stockCode(parseCode(stockDto.getStockCode()))
                .stockName(stockDto.getStockName())
                .board(BoardEnum.getBoardByCode(stockDto.getStockCode()))
                .stat(stat)
                .listingDate(offsetDate)
                .build();
    }

    /**
     * 每分钟最多调用 500 次（每秒最多调用 8 次）
     * @param startDate yyyy-MM-dd 开始时间，包含，为空则不过滤; 例如 2021-01-01
     * @param endDate   yyyy-MM-dd 结束时间，包含，为空则不过滤; 例如 2021-01-02
     * @param codes     股票代码列表，为空则表示所有，例如 [000001, 000002]
     * @return
     */
    @Override
    public Flux<KLineDto> crawlKLine(@Nullable String startDate, @Nullable String endDate, String... codes) {
        // 爬取所有股票 K 线
        return splitCodes(startDate, endDate, codes)
                .flatMap(tuple -> {
                    // 每分钟最多调用 500 次（每秒最多调用 8 次）
                    try {
                        SyncUtils.waitMillis(1000/8);
                    } catch (InterruptedException e) {
                        logger.error("Interrupted", e);
                    }
                    return Tushare.Daily.builder()
                            .ts_code(formatCodes(tuple.getT3()))
                            .start_date(yyyyMMdd(tuple.getT1()))
                            .end_date(yyyyMMdd(tuple.getT2()))
                            .build()
                            .req()
                            .flatMap(response -> {
                                try {
                                    String body = response.body().string();
                                    // 获取映射文件
                                    File file = ResourceUtils.getFile("classpath:tushare/daily_2_k_line.yml");
                                    return CsvMapper.yamlFile(TushareResp.FIELDS_PATH, TushareResp.ITEMS_PATH, file)
                                            .eval(body, KLineDto.class)
                                            .map(this::format);
                                } catch (IOException e) {
                                    logger.error("failed to call tushare.daily");
                                }
                                return Flux.just();
                            });
                });
    }

    /**
     * tushare 最多返回 5000 条记录, 为了不使请求数据丢失，
     * 这里将 codes 分割，使其在 规定的日期内最多返回 5000 条数据
     * @return startDate, endDate, codes
     */
    // 返回最大记录数，日线每日 1 条，最多 5000 条
    private static final long MAX_ITEMS = 5000L;
    // 每次请求 codes 最大值
    private static final int MAX_CODES = 100;
    private static Flux<Tuple3<String, String, String[]>> splitCodes(String startDate, String endDate, String[] codes) {
        if (ArrayUtil.isEmpty(codes)) {
            return Flux.just(Tuples.of(startDate, endDate, new String[]{}));
        }

        return Flux.create(sink -> {
            LocalDate start = DateUtils.parseDate(startDate);
            LocalDate end = DateUtils.parseDate(endDate);

            // 最多间隔 5000 天（每天一条数据）
            long diff;
            while ((diff = start.until(end, ChronoUnit.DAYS)) >= MAX_ITEMS) {
                // 没个 code 生成 MAX_ITEMS 条数据
                String from = DateUtils.formatDate(start);
                String to = DateUtils.formatDate(start.plusDays(MAX_ITEMS - 1));
                Flux.fromArray(codes)
                        .subscribe(code -> {
                            sink.next(Tuples.of(from, to, new String[]{code}));
                        });

                // 继续校验是否超过
                start = start.plusDays(MAX_ITEMS);
            }

            // 获取日期区间
            String startStr = DateUtils.formatDate(start);
            String endStr = DateUtils.formatDate(end);

            // 计算每次循环的 codes 数
            // codes
            int div = Math.min(Math.toIntExact(MAX_ITEMS / diff), MAX_CODES);
            int len = codes.length;

            int from = 0;
            int to = div;
            while (to <= len) {
                String[] range = Arrays.copyOfRange(codes, from, to);
                sink.next(Tuples.of(startStr, endStr, range));
                from = to;
                to = to + div;
            }

            // 剩余的 codes
            if (from < len) {
                String[] range = Arrays.copyOfRange(codes, from, len);
                sink.next(Tuples.of(startStr, endStr, range));
            }

            sink.complete();
        });


    }

    /**
     * 格式化字段格式
     * @param kLineDto
     * @return
     */
    private KLineDto format(KLineDto kLineDto) {
        // 000001.SZ -> 000001
        kLineDto.setStockCode(TushareUtil.removeSuffix(kLineDto.getStockCode()));
        // yyyyMMdd -> yyyy-MM-dd
        kLineDto.setDate(formatDate(kLineDto.getDate()));
        // 成交量 tushare 单位是手, 改为股
        kLineDto.setVolume(getVolShares(kLineDto.getVolume()));
        // 成交额 tushare 单位：千元，改为 元
        kLineDto.setAmount(getAmountYuan(kLineDto.getAmount()));
        return kLineDto;
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

    // 空方法
    public void emptyMethod() {}

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
