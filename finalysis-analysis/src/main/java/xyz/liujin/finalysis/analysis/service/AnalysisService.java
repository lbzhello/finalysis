package xyz.liujin.finalysis.analysis.service;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.*;
import xyz.liujin.finalysis.analysis.mapper.AnalysisMapper;
import xyz.liujin.finalysis.analysis.strategy.AnalysisStrategy;
import xyz.liujin.finalysis.base.page.PageQo;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.dto.DailyData;
import xyz.liujin.finalysis.daily.service.AvgLineService;
import xyz.liujin.finalysis.daily.service.DailyIndicatorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.*;

@Service
public class AnalysisService {
    @Autowired
    private AnalysisMapper analysisMapper;

    @Autowired
    private AvgLineService avgLineService;

    @Autowired
    private AnalysisStrategy analysisStrategy;

    @Autowired
    private RecommendService recommendService;

    /**
     * 股票日指标详情信息
     * @param dailyDataQo
     * @return
     */
    public Flux<DailyData> dailyData(DailyDataQo dailyDataQo) {
        List<DailyData> dailyData = analysisMapper.dailyData(dailyDataQo);
        if (CollectionUtil.isEmpty(dailyData)) {
            return Flux.empty();
        }
        return Flux.fromIterable(dailyData);
    }

    @Autowired
    private DailyIndicatorService dailyIndicatorService;

    /**
     * 推荐股票
     * @return
     */
    public Flux<DailyData> recommend(RecommendQo recommendQo) {
        // 推荐日期，默认数据库最新或当天
        LocalDate date = ObjectUtils.firstNonNull(recommendQo.getDate(), dailyIndicatorService.getLatestDate(), LocalDate.now());

        // 获取最近 10 天内，存在量比大于 2 的股票
        List<Flux<String>> recommends = new ArrayList<>();

        // 放量指标
        HeavenVolRatioQo heavenVolRatio = recommendQo.getHeavenVolRatio();
        // 日期默认推荐日期
        if (Objects.nonNull(heavenVolRatio) && Objects.isNull(heavenVolRatio.getDate())) {
            heavenVolRatio.setDate(date);
        }

        // 均线突破指标
        FiveCrossTenQo fiveCrossTen = recommendQo.getFiveCrossTen();
        // 日期默认推荐日期
        if (Objects.nonNull(fiveCrossTen) && Objects.isNull(fiveCrossTen.getDate())) {
            fiveCrossTen.setDate(date);
        }

        // 增长趋势指标
        FiveAboveTenQo fiveAboveTen = recommendQo.getFiveAboveTen();
        // 日期默认推荐日期
        if (Objects.nonNull(fiveAboveTen) && Objects.isNull(fiveAboveTen.getDate())) {
            fiveAboveTen.setDate(date);
        }

        // 放量指标
        if (Objects.nonNull(heavenVolRatio)) {
            recommends.add(analysisStrategy.heavenVolumeRatio(heavenVolRatio));
        }

        // 日线突破指标、上升趋势指标取并集
        if (Objects.nonNull(fiveCrossTen) && Objects.nonNull(fiveAboveTen)) {
            recommends.add(analysisStrategy.fiveCrossTen(fiveCrossTen)
                    .concatWith(analysisStrategy.fiveAboveTen(fiveAboveTen))
                    .collectList()
                    .flux()
                    .flatMap(codes -> Flux.fromIterable(new HashSet<>(codes))));
        } else {
            // 日线突破
            if (Objects.nonNull(fiveCrossTen)) {
                recommends.add(analysisStrategy.fiveCrossTen(fiveCrossTen));
            }
            // 上升趋势
            if (Objects.nonNull(fiveAboveTen)) {
                recommends.add(analysisStrategy.fiveAboveTen(fiveAboveTen));
            }
        }

        // 默认根据量比排序
        String orderBy = Optional.ofNullable(recommendQo.getPage())
                .map(PageQo::getOrderBy)
                .orElse("volume_ratio desc");

        // 返回条目限制，默认 1000
        Integer limit = Optional.ofNullable(recommendQo.getPage())
                .map(PageQo::getLimit)
                .orElse(1000);

        return Flux.fromIterable(recommends)
                .flatMap(codeFlux -> codeFlux.collectList().flux())
                // 取交集，即满足所有指标的股票
                .reduce((left, right) -> {
                    left.retainAll(right);
                    return left;
                })
                .flux()
                .doOnNext(codes -> {
                    // 是否将推荐股票入库，方便以后统计
                    if (recommendQo.isStore()) {
                        recommendService.refreshRecommend(date, codes);
                    }
                })
                .flatMap(codes -> dailyData(DailyDataQo.builder()
                        .date(date)
                        .codes(codes)
                        .minAmount(recommendQo.getMinAmount())
                        .page(PageQo.builder()
                                .limit(limit)
                                .orderBy(orderBy)
                                .build())
                        .build()))
                .limitRequest(limit);
    }

    /**
     * 最近 days 天内存在量比大于等于 minVolRatio 的股票详情
     * @param days 最近天数
     * @return
     */
    public Flux<DailyData> heavenVolumeRatioDetail(int days, BigDecimal minVolRatio) {
        return analysisStrategy.heavenVolumeRatio(HeavenVolRatioQo.builder()
                .days(days)
                .minVolRatio(minVolRatio)
                .build())
                .collectList()
                .flux()
                .flatMap(codes -> {
                    return dailyData(DailyDataQo.builder()
                            .date(dailyIndicatorService.getLatestDate())
                            .codes(codes)
                            .page(PageQo.builder()
                                    .orderBy("volume_ratio desc")
                                    .build())
                            .build());
                });

    }

//    /**
//     * 最近 days 天内存在量比大于等于 minVolRatio 的股票
//     * @param days 最近天数
//     * @return
//     */
//    public List<String> heavenVolumeRatio(int days, BigDecimal minVolRatio) {
//        // 计算天数区间
//        LocalDate end  = dailyIndicatorService.getLatestDate();
//        LocalDate start = end.minusDays(days - 1);
//
//        return analysisMapper.heavenVolumeRatio(start, end, minVolRatio);
//    }

    /**
     * 5 日线超 10 日线股票详情信息
     * @param days
     * @param date
     * @return
     */
    public Flux<DailyData> fiveAboveTenDetail(Integer days, LocalDate date) {
        return avgLineService.fiveAboveTen(days, date, null)
                .collectList()
                .flux()
                .flatMap(codes -> CollectionUtil.isEmpty(codes) ? Flux.empty() :
                        dailyData(DailyDataQo.builder()
                        // 日期默认当前最新数据
                        .date(ObjectUtils.firstNonNull(date, avgLineService.getLatestDate(), LocalDate.now()))
                        .codes(codes)
                        .build()));
    }

    /**
     * 获取 5 日线突破 10 日线的股票详情
     * @param days 最大突破天数，最多十天
     * @return
     */
    public Flux<DailyData> fiveCrossTenDetail(Integer days, LocalDate date) {
        return avgLineService.fiveCrossTen(days, date, null)
                .collectList()
                .flux()
                .flatMap(codes -> CollectionUtil.isEmpty(codes) ? Flux.empty() :
                        dailyData(DailyDataQo.builder()
                        .date(ObjectUtils.firstNonNull(date, avgLineService.getLatestDate(), LocalDate.now()))
                        .codes(codes)
                        .build()));
    }


}
