package xyz.liujin.finalysis.extractor.tushare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.base.json.CsvMapper;
import xyz.liujin.finalysis.daily.entity.DailyIndicator;
import xyz.liujin.finalysis.extractor.tushare.api.Tushare;
import xyz.liujin.finalysis.extractor.tushare.api.TushareResp;
import xyz.liujin.finalysis.extractor.tushare.dto.TushareDailyIndicator;
import xyz.liujin.finalysis.extractor.tushare.util.TushareUtil;

import java.io.IOException;
import java.time.LocalDate;
import java.util.List;

/**
 * 获取股票每日指标
 */
@Component
public class DailyIndicatorExtractor {
    private static Logger logger = LoggerFactory.getLogger(DailyIndicatorExtractor.class);

    public Flux<DailyIndicator> extractDailyIndicator(LocalDate start, LocalDate end, List<String> codes) {
        return TushareUtil.splitCodes(start, end, codes)
                .flatMap(tuple -> Tushare.DailyBasic.builder()
                        .ts_code(TushareUtil.formatCodes(tuple.getT3()))
                        .start_date(TushareUtil.formatDate(tuple.getT1()))
                        .end_date(TushareUtil.formatDate(tuple.getT2()))
                        .build()
                        .req("")
                        .flatMap(response -> {
                            try {
                                String bodyStr = response.body().string();
                                // 字段映射
                                return CsvMapper.create(TushareResp.FIELDS_PATH, TushareResp.ITEMS_PATH)
                                        .eval(bodyStr, TushareDailyIndicator.class)
                                        .map(this::toDailyIndicator);

                            } catch (IOException e) {
                                logger.error("failed to extract daily indicator", e);
                            }

                            return Flux.just();
                        }));

    }

    // 转成数据库对象
    public DailyIndicator toDailyIndicator(TushareDailyIndicator tushareDailyIndicator) {
        return DailyIndicator.builder()
                .stockCode(TushareUtil.parseCode(tushareDailyIndicator.getTs_code()))
                .date(TushareUtil.parseDate(tushareDailyIndicator.getTrade_date()))
                .close(TushareUtil.parseBigDecimal(tushareDailyIndicator.getClose()))
                .turnoverRate(TushareUtil.parseBigDecimal(tushareDailyIndicator.getTurnover_rate()))
                .turnoverRateF(TushareUtil.parseBigDecimal(tushareDailyIndicator.getTurnover_rate_f()))
                .volumeRatio(TushareUtil.parseBigDecimal(tushareDailyIndicator.getVolume_ratio()))
                .pe(TushareUtil.parseBigDecimal(tushareDailyIndicator.getPe()))
                .peTtm(TushareUtil.parseBigDecimal(tushareDailyIndicator.getPe_ttm()))
                .pb(TushareUtil.parseBigDecimal(tushareDailyIndicator.getPb()))
                .ps(TushareUtil.parseBigDecimal(tushareDailyIndicator.getPs()))
                .psTtm(TushareUtil.parseBigDecimal(tushareDailyIndicator.getPs_ttm()))
                .dvRatio(TushareUtil.parseBigDecimal(tushareDailyIndicator.getDv_ratio()))
                .dvTtm(TushareUtil.parseBigDecimal(tushareDailyIndicator.getDv_ttm()))
                // 万股转股
                .totalShare(TushareUtil.parseBigDecimal(tushareDailyIndicator.getTotal_share(), 10000))
                // 万股转股
                .floatShare(TushareUtil.parseBigDecimal(tushareDailyIndicator.getFloat_share(), 10000))
                // 万股转股
                .freeShare(TushareUtil.parseBigDecimal(tushareDailyIndicator.getFree_share(), 10000))
                // 万元转元
                .totalMv(TushareUtil.parseBigDecimal(tushareDailyIndicator.getTotal_mv(), 10000))
                // 万元转元
                .circMv(TushareUtil.parseBigDecimal(tushareDailyIndicator.getCirc_mv(), 10000))
                .build();
    }
}
