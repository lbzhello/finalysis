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

    public Flux<DailyData> dailyData(DailyDateQo req) {
        List<DailyData> dailyData = dailyAnalysisMapper.dailyData(req);
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
        // 日期默认当前最新数据
        LocalDate curDate = ObjectUtils.firstNonNull(date, avgLineService.getLatestDate(), LocalDate.now());
        avgLineService.fiveAboveTen(days, date)
                .collectList()
                .flux()
                .flatMap(codes -> {
                    if (CollectionUtil.isEmpty(codes)) {
                        return Flux.just();
                    }


                    return null;

                });
        return null;
    }

}
