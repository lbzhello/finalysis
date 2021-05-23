package xyz.liujin.finalysis.analysis.mapper;

import org.apache.ibatis.annotations.Param;
import xyz.liujin.finalysis.analysis.dto.DailyDateQo;
import xyz.liujin.finalysis.daily.dto.DailyData;

import java.time.LocalDate;
import java.util.List;

public interface AnalysisMapper {
    /**
     * 获取股票日数据
     * @param dailyDateQo
     * @return
     */
    List<DailyData> dailyData(DailyDateQo dailyDateQo);

    /**
     * 最近（几天内放量的股票）
     * @return
     */
    List<String> heavenVolumeRatio(@Param("startDate") LocalDate startDate);
}
