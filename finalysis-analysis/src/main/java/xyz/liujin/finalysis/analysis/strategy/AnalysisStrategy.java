package xyz.liujin.finalysis.analysis.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.FiveAboveTenQo;
import xyz.liujin.finalysis.analysis.dto.FiveCrossTenQo;
import xyz.liujin.finalysis.analysis.dto.HeavenVolRatioQo;
import xyz.liujin.finalysis.analysis.mapper.AnalysisMapper;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.service.AvgLineService;
import xyz.liujin.finalysis.daily.service.DailyIndicatorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 股票分析策略
 * 用于股票分析时从某个纬度选择股票
 */
@Component
public class AnalysisStrategy {
    public static final String HOT_NEWS = "HOT_NEWS"; // "热点消息面
    public static final String FIVE_CROSS_TEN = "FIVE_CROSS_TEN"; // "5 日线突破 10 日线
    public static final String FIVE_ABOVE_TEN = "FIVE_ABOVE_TEN"; // "5 日线在 10 日线上方
    public static final String HEAVY_VOL_RATIO = "HEAVY_VOL"; // "放量
    public static final String NIL = "NIL"; // "无

    @Autowired
    private AnalysisMapper analysisMapper;

    @Autowired
    private DailyIndicatorService dailyIndicatorService;

    @Autowired
    private AvgLineService avgLineService;

    /**
     * 最近 days 天内存在量比大于等于 minVolRatio 的股票
     * @return
     */
    public Flux<String> heavenVolumeRatio(@Nullable HeavenVolRatioQo heavenVolRatioQo) {
        heavenVolRatioQo = Optional.ofNullable(heavenVolRatioQo).orElse(HeavenVolRatioQo.DEFAULT);
        int days = Optional.ofNullable(heavenVolRatioQo.getDays()).orElse(6);
        BigDecimal minVolRatio = Optional.ofNullable(heavenVolRatioQo.getMinVolRatio()).orElse(BigDecimal.valueOf(2));
        List<String> codes = Optional.ofNullable(heavenVolRatioQo.getCodes()).orElse(null);

        // 结束日期默认数据库最新，或者当前日期
        LocalDate end = ObjectUtils.firstNonNull(heavenVolRatioQo.getDate(), dailyIndicatorService.getLatestDate(), LocalDate.now());

        // 开始日期根据 days 计算
        LocalDate start = end.minusDays(days - 1);

        return Flux.fromIterable(analysisMapper.heavenVolumeRatio(start, end, minVolRatio, codes));
    }

    /**
     * 获取 5 日线突破（上穿） 10 日线的股票
     * @param fiveCrossTenQo
     * @return
     */
    public Flux<String> fiveCrossTen(@Nullable FiveCrossTenQo fiveCrossTenQo) {
        fiveCrossTenQo = Optional.ofNullable(fiveCrossTenQo).orElse(FiveCrossTenQo.DEFAULT);
        Integer days = Optional.ofNullable(fiveCrossTenQo.getDays()).orElse(3);
        LocalDate date = Optional.ofNullable(fiveCrossTenQo.getDate()).orElse(null);
        List<String> codes = Optional.ofNullable(fiveCrossTenQo.getCodes()).orElse(null);
        return avgLineService.fiveCrossTen(days, date, codes);
    }

    /**
     * 获取 5 日线在 10 日线上方的股票
     * @param fiveAboveTenQo
     * @return
     */
    public Flux<String> fiveAboveTen(@Nullable FiveAboveTenQo fiveAboveTenQo) {
        fiveAboveTenQo = Optional.ofNullable(fiveAboveTenQo).orElse(FiveAboveTenQo.DEFAULT);
        Integer days = Optional.ofNullable(fiveAboveTenQo.getDays()).orElse(3);
        LocalDate date = Optional.ofNullable(fiveAboveTenQo.getDate()).orElse(null);
        List<String> codes = Optional.ofNullable(fiveAboveTenQo.getCodes()).orElse(null);
        return avgLineService.fiveAboveTen(days, date, codes);
    }

}
