package xyz.liujin.finalysis.analysis.service;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.DailyDateQo;
import xyz.liujin.finalysis.analysis.mapper.AnalysisMapper;
import xyz.liujin.finalysis.base.page.PageQo;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.dto.DailyData;
import xyz.liujin.finalysis.daily.service.AvgLineService;
import xyz.liujin.finalysis.daily.service.DailyIndicatorService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Service
public class AnalysisService {
    @Autowired
    private AnalysisMapper analysisMapper;

    @Autowired
    private AvgLineService avgLineService;

    /**
     * 股票日指标详情信息
     * @param dailyDateQo
     * @return
     */
    public Flux<DailyData> dailyData(DailyDateQo dailyDateQo) {
        List<DailyData> dailyData = analysisMapper.dailyData(dailyDateQo);
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
        // todo 增加底部放量的股票
        return avgLineService.fiveCrossTen(3, null)
                .concatWith(avgLineService.fiveAboveTen(3, null))
                .collectList()
                .flux()
                .flatMap(codes -> {
                    return dailyData(DailyDateQo.builder()
                            .date(dailyIndicatorService.getLatestDate())
                            .codes(codes)
                            .minAmount(BigDecimal.valueOf(1e9))
                            .page(PageQo.builder()
                                    .orderBy("volume_ratio desc")
                                    .build())
                            .build());
                })
                // todo 查找量比层大于 2？ 的股票
                .map(dailyData -> dailyData)
                .limitRequest(100);
    }

    /**
     * 最近放量的股票
     * @param days 最近天数
     * @return
     */
    public Flux<DailyData> heavenVolumeRatio(Integer days) {
        List<String> list = analysisMapper. heavenVolumeRatio(LocalDate.of(2021, 5, 21));
        return null;
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
                        dailyData(DailyDateQo.builder()
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
                        dailyData(DailyDateQo.builder()
                        .date(ObjectUtils.firstNonNull(date, avgLineService.getLatestDate(), LocalDate.now()))
                        .codes(codes)
                        .build()));
    }


}
