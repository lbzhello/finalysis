package xyz.liujin.finalysis.analysis.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.FiveAboveTenQo;
import xyz.liujin.finalysis.analysis.dto.FiveCrossTenQo;
import xyz.liujin.finalysis.analysis.dto.HeavenVolRatioQo;
import xyz.liujin.finalysis.analysis.dto.RecommendQo;
import xyz.liujin.finalysis.analysis.mapper.AnalysisMapper;
import xyz.liujin.finalysis.daily.service.AvgLineService;
import xyz.liujin.finalysis.daily.service.DailyIndicatorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;

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
        int days = Optional.ofNullable(heavenVolRatioQo).map(HeavenVolRatioQo::getDays).orElse(10);
        BigDecimal minVolRatio = Optional.ofNullable(heavenVolRatioQo).map(HeavenVolRatioQo::getMinVolRatio).orElse(BigDecimal.valueOf(1.5));
        List<String> codes = Optional.ofNullable(heavenVolRatioQo).map(HeavenVolRatioQo::getCodes).orElse(null);

        // 计算天数区间
        LocalDate end  = dailyIndicatorService.getLatestDate();
        LocalDate start = end.minusDays(days - 1);

        return Flux.fromIterable(analysisMapper.heavenVolumeRatio(start, end, minVolRatio, codes));
    }

    /**
     * 获取 5 日线突破（上穿） 10 日线的股票
     * @param fiveCrossTenQo
     * @return
     */
    public Flux<String> fiveCrossTen(@Nullable FiveCrossTenQo fiveCrossTenQo) {
        Integer days = Optional.ofNullable(fiveCrossTenQo).map(FiveCrossTenQo::getDays).orElse(3);
        LocalDate date = Optional.ofNullable(fiveCrossTenQo).map(FiveCrossTenQo::getDate).orElse(null);
        List<String> codes = Optional.ofNullable(fiveCrossTenQo).map(FiveCrossTenQo::getCodes).orElse(null);
        return avgLineService.fiveCrossTen(days, date, codes);
    }

    /**
     * 获取 5 日线在 10 日线上方的股票
     * @param fiveAboveTenQo
     * @return
     */
    public Flux<String> fiveAboveTen(@Nullable FiveAboveTenQo fiveAboveTenQo) {
        Integer days = Optional.ofNullable(fiveAboveTenQo).map(FiveAboveTenQo::getDays).orElse(3);
        LocalDate date = Optional.ofNullable(fiveAboveTenQo).map(FiveAboveTenQo::getDate).orElse(null);
        return avgLineService.fiveAboveTen(days, date);
    }

    /**
     * 获取推荐股票时需要参考的指标
     * 判断条件：对应字段非 null
     * @return
     */
    static Set<AnalysisStrategy> findStrategies(@Nullable RecommendQo recommendQo) {
        if (Objects.isNull(recommendQo)) {
            return Set.of();
        }

//        Set<AnalysisStrategy> analysisStrategies = new HashSet<>();
//        if (Objects.nonNull(heavenVolRatio)) {
//            analysisStrategies.add(AnalysisStrategy.HEAVY_VOL_RATIO);
//        }
//
//        if (Objects.nonNull(fiveCrossTen)) {
//            analysisStrategies.add(AnalysisStrategy.FIVE_CROSS_TEN);
//        }
//
//        if (Objects.nonNull(fiveAboveTen)) {
//            analysisStrategies.add(AnalysisStrategy.FIVE_ABOVE_TEN);
//        }

        return null;
    }
}
