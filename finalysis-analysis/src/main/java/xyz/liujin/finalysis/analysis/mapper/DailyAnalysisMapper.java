package xyz.liujin.finalysis.analysis.mapper;

import xyz.liujin.finalysis.analysis.dto.DailyDateReq;
import xyz.liujin.finalysis.daily.dto.DailyData;

import java.util.List;

public interface DailyAnalysisMapper {
    /**
     * 获取股票日数据
     * @param req
     * @return
     */
    List<DailyData> dailyData(DailyDateReq req);
}
