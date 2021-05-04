package xyz.liujin.finalysis.extractor.tushare;

import cn.hutool.core.text.CharSequenceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.base.constant.StockBoardEnum;
import xyz.liujin.finalysis.base.constant.StockConst;
import xyz.liujin.finalysis.base.json.CsvMapper;
import xyz.liujin.finalysis.base.util.DateUtils;
import xyz.liujin.finalysis.base.util.SyncUnit;
import xyz.liujin.finalysis.daily.dto.KLineDto;
import xyz.liujin.finalysis.extractor.KLineExtractor;
import xyz.liujin.finalysis.extractor.StockExtractor;
import xyz.liujin.finalysis.extractor.tushare.api.Tushare;
import xyz.liujin.finalysis.extractor.tushare.api.TushareResp;
import xyz.liujin.finalysis.extractor.tushare.dto.TushareStock;
import xyz.liujin.finalysis.extractor.tushare.util.TushareUtil;
import xyz.liujin.finalysis.stock.entity.Stock;

import java.io.File;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

/**
 * Tushare K 线数据爬取
 */
@Component(StockExtractor.TUSHARE)
public class TushareExtractor implements StockExtractor, KLineExtractor {
    private static Logger logger = LoggerFactory.getLogger(TushareExtractor.class);

    @Override
    public Flux<Stock> extractStock() {
        return Tushare.StockBasic.builder()
                .build()
                .req("symbol,name,list_status,list_date")
                .flatMap(response -> {
                    try {
                        String bodyStr = response.body().string();
                        // 获取映射文件
                        File file = ResourceUtils.getFile("classpath:tushare/stock_basic_2_stock.yml");
                        return CsvMapper.yamlFile(TushareResp.FIELDS_PATH, TushareResp.ITEMS_PATH, file)
                                .eval(bodyStr, TushareStock.class)
                                .map(this::toStock);
                    } catch (Exception e) {
                        logger.error("extract stock failed", e);
                    }
                    return Flux.just();
                });
    }

    private Stock toStock(TushareStock tushareStock) {
        // 股票状态
        String statStr = tushareStock.getStat();
        int stat = 0;
        if (Objects.equals(statStr, "L")) {
            stat = StockConst.NORMAL;
        } else if (Objects.equals(statStr, "D")) {
            stat = StockConst.DE_LISTING;
        } else if (Objects.equals(statStr, "P")) {
            stat = StockConst.PAUSE_LISTING;
        }
        String dateStr = tushareStock.getListingDate();
        LocalDate offsetDate = DateUtils.parseDate(dateStr, "yyyyMMdd");

        return Stock.builder()
                .stockCode(TushareUtil.parseCode(tushareStock.getStockCode()))
                .stockName(tushareStock.getStockName())
                .board(StockBoardEnum.getBoardByCode(tushareStock.getStockCode()))
                .stat(stat)
                .listingDate(offsetDate)
                .build();
    }

    // 用于控制方法执行速率
    private SyncUnit syncUnit = SyncUnit.create();
    /**
     * 每分钟最多调用 500 次（每秒最多调用 8 次）
     * @param startDate yyyy-MM-dd 开始时间，包含，为空则不过滤; 例如 2021-01-01
     * @param endDate   yyyy-MM-dd 结束时间，包含，为空则不过滤; 例如 2021-01-02
     * @param codes     股票代码列表，为空则表示所有，例如 [000001, 000002]
     * @return
     */
    @Override
    public Flux<KLineDto> extractKLine(@Nullable LocalDate startDate, @Nullable LocalDate endDate, List<String> codes) {
        // 爬取所有股票 K 线
        return TushareUtil.splitCodes(startDate, endDate, codes)
                .flatMap(tuple -> {
                    // 每分钟最多调用 500 次（每秒最多调用 8 次）
                    syncUnit.waitMillis(1000/8);

                    return Tushare.Daily.builder()
                            .ts_code(TushareUtil.formatCodes(tuple.getT3()))
                            .start_date(TushareUtil.formatDate(tuple.getT1()))
                            .end_date(TushareUtil.formatDate(tuple.getT2()))
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
     * 格式化字段格式
     * @param kLineDto
     * @return
     */
    private KLineDto format(KLineDto kLineDto) {
        // 000001.SZ -> 000001
        kLineDto.setStockCode(TushareUtil.parseCode(kLineDto.getStockCode()));
        // yyyyMMdd -> yyyy-MM-dd
        kLineDto.setDate(formatDate(kLineDto.getDate()));
        // 成交量 tushare 单位是手, 改为股
        kLineDto.setVolume(getVolShares(kLineDto.getVolume()));
        // 成交额 tushare 单位：千元，改为 元
        kLineDto.setAmount(getAmountYuan(kLineDto.getAmount()));
        return kLineDto;
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
