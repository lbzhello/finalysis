package xyz.liujin.finalysis.analysis.mapper;

import xyz.liujin.finalysis.analysis.dto.DailyDateQo;
import xyz.liujin.finalysis.daily.dto.DailyData;

import java.util.List;

public interface AnalysisMapper {
    /**
     * 获取股票日数据
     * @param dailyDateQo
     * @return
     */
    List<DailyData> dailyData(DailyDateQo dailyDateQo);
}
