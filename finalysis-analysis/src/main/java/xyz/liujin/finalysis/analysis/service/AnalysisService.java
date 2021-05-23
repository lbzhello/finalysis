package xyz.liujin.finalysis.analysis.service;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.DailyDataQo;
import xyz.liujin.finalysis.analysis.mapper.AnalysisMapper;
import xyz.liujin.finalysis.base.page.PageQo;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.dto.DailyData;
import xyz.liujin.finalysis.daily.service.AvgLineService;
import xyz.liujin.finalysis.daily.service.DailyIndicatorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;

@Service
public class AnalysisService {
    @Autowired
    private AnalysisMapper analysisMapper;

    @Autowired
    private AvgLineService avgLineService;

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
    public Flux<DailyData> recommend() {
        // 获取最近 10 天内，存在量比大于 2 的股票
        Set<String> heavenVolRatioCodes = Set.copyOf(heavenVolumeRatio(10, BigDecimal.valueOf(1.5)));
        return avgLineService.fiveCrossTen(3, null)
                .concatWith(avgLineService.fiveAboveTen(3, null))
                .collectList()
                .flux()
                .flatMap(codes -> {
                    boolean b = codes.retainAll(heavenVolRatioCodes);
                    return dailyData(DailyDataQo.builder()
                            .date(dailyIndicatorService.getLatestDate())
                            .codes(codes)
                            .minAmount(BigDecimal.valueOf(1e9))
                            .page(PageQo.builder()
                                    .orderBy("volume_ratio desc")
                                    .build())
                            .build());
                })
                .map(dailyData -> dailyData)
                .limitRequest(100);
    }

    /**
     * 最近 days 天内存在量比大于等于 minVolRatio 的股票详情
     * @param days 最近天数
     * @return
     */
    public Flux<DailyData> heavenVolumeRatioDetail(int days, BigDecimal minVolRatio) {
        return dailyData(DailyDataQo.builder()
                .date(dailyIndicatorService.getLatestDate())
                .codes(heavenVolumeRatio(days, minVolRatio))
                .page(PageQo.builder()
                        .orderBy("volume_ratio desc")
                        .build())
                .build());
    }

    /**
     * 最近 days 天内存在量比大于等于 minVolRatio 的股票
     * @param days 最近天数
     * @return
     */
    public List<String> heavenVolumeRatio(int days, BigDecimal minVolRatio) {
        // 计算天数区间
        LocalDate end  = dailyIndicatorService.getLatestDate();
        LocalDate start = end.minusDays(days - 1);

        return analysisMapper.heavenVolumeRatio(start, end, minVolRatio);
    }

    /**
     * 5 日线超 10 日线股票详情信息
     * @param days
     * @param date
     * @return
     */
    public Flux<DailyData> fiveAboveTenDetail(Integer days, LocalDate date) {
        return avgLineService.fiveAboveTen(days, date)
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
        return avgLineService.fiveCrossTen(days, date)
                .collectList()
                .flux()
                .flatMap(codes -> CollectionUtil.isEmpty(codes) ? Flux.empty() :
                        dailyData(DailyDataQo.builder()
                        .date(ObjectUtils.firstNonNull(date, avgLineService.getLatestDate(), LocalDate.now()))
                        .codes(codes)
                        .build()));
    }


}
