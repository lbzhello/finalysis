package xyz.liujin.finalysis.analysis.service;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.DailyDateQo;
import xyz.liujin.finalysis.analysis.mapper.DailyAnalysisMapper;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.dto.DailyData;
import xyz.liujin.finalysis.daily.service.AvgLineService;

import java.time.LocalDate;
import java.util.List;

@Service
public class DailyAnalysisService {
    @Autowired
    private DailyAnalysisMapper dailyAnalysisMapper;

    @Autowired
    private AvgLineService avgLineService;

    /**
     * 股票日指标详情信息
     * @param dailyDateQo
     * @return
     */
    public Flux<DailyData> dailyData(DailyDateQo dailyDateQo) {
        List<DailyData> dailyData = dailyAnalysisMapper.dailyData(dailyDateQo);
        if (CollectionUtil.isEmpty(dailyData)) {
            return Flux.empty();
        }
        return Flux.fromIterable(dailyData);
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
                .flatMap(codes -> dailyData(DailyDateQo.builder()
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
                .flatMap(codes -> dailyData(DailyDateQo.builder()
                        .date(ObjectUtils.firstNonNull(date, avgLineService.getLatestDate(), LocalDate.now()))
                        .codes(codes)
                        .build()));
    }
}
