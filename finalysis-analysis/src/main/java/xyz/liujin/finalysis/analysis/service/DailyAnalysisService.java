package xyz.liujin.finalysis.analysis.service;

import cn.hutool.core.collection.CollectionUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.DailyDateReq;
import xyz.liujin.finalysis.analysis.mapper.DailyAnalysisMapper;
import xyz.liujin.finalysis.daily.dto.DailyData;

import java.util.List;

@Service
public class DailyAnalysisService {
    @Autowired
    private DailyAnalysisMapper dailyAnalysisMapper;

    public Flux<DailyData> dailyData(DailyDateReq req) {
        List<DailyData> dailyData = dailyAnalysisMapper.dailyData(req);
        if (CollectionUtil.isEmpty(dailyData)) {
            return Flux.empty();
        }
        return Flux.fromIterable(dailyData);
    }

}
