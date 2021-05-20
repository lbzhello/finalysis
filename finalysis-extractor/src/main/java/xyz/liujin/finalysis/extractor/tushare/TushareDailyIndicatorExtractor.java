package xyz.liujin.finalysis.extractor.tushare;

import cn.hutool.json.JSONUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.base.util.JsonUtils;
import xyz.liujin.finalysis.base.util.SyncUnit;
import xyz.liujin.finalysis.daily.entity.DailyIndicator;
import xyz.liujin.finalysis.extractor.tushare.api.Tushare;
import xyz.liujin.finalysis.extractor.tushare.api.TushareResp;
import xyz.liujin.finalysis.extractor.tushare.dto.TushareDailyIndicator;
import xyz.liujin.finalysis.extractor.tushare.util.TushareUtil;

import java.time.LocalDate;
import java.util.List;

/**
 * 获取股票每日指标
 */
@Component
public class TushareDailyIndicatorExtractor {
    private static Logger logger = LoggerFactory.getLogger(TushareDailyIndicatorExtractor.class);

    // 用于控制方法执行速率 每分钟最多访问该接口200次
    private SyncUnit syncUnit = SyncUnit.create();
    /**
     * 获取股票每日指标数据
     * 每分钟最多访问该接口200次
     * @param start 开始日期
     * @param end 结束日期
     * @param codes 股票列表；如 ["000001", "600001"...]
     * @return
     */
    public Flux<DailyIndicator> extractDailyIndicator(LocalDate start, LocalDate end, List<String> codes) {
        return TushareUtil.splitCodes(start, end, codes)
                .flatMap(tuple -> {
                    // 每分钟最多调用 200 次
                    syncUnit.waitMillis(300);
                    return Tushare.DailyBasic.builder()
                            .ts_code(TushareUtil.formatCodes(tuple.getT3()))
                            .start_date(TushareUtil.formatDate(tuple.getT1()))
                            .end_date(TushareUtil.formatDate(tuple.getT2()))
                            .build()
                            .reqBody()
                            .flatMap(bodyStr -> {
                                try {
                                    TushareResp tushareResp = JSONUtil.toBean(bodyStr, TushareResp.class);

                                    if (TushareUtil.hasError(tushareResp)) {
                                        IllegalStateException illegalStateException = new IllegalStateException(tushareResp.getMsg());
                                        logger.error("failed to extract tushare daily indicator", illegalStateException);
                                        return Flux.error(illegalStateException);
                                    }

                                    return JsonUtils.parseCsv(tushareResp.getData().getFields(), tushareResp.getData().getItems())
                                            .map(map -> JSONUtil.toBean(JSONUtil.parseObj(map), TushareDailyIndicator.class))
                                            .map(this::toDailyIndicator);

                                } catch (Exception e) {
                                    logger.error("failed to extract daily indicator", e);
                                }

                                return Flux.just();
                            });
                });

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
