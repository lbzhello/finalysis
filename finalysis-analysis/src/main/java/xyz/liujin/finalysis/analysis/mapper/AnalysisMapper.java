package xyz.liujin.finalysis.analysis.mapper;

import org.apache.ibatis.annotations.Param;
import org.springframework.lang.Nullable;
import xyz.liujin.finalysis.analysis.dto.DailyDataQo;
import xyz.liujin.finalysis.daily.dto.DailyData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface AnalysisMapper {
    /**
     * 获取股票日数据
     * @param dailyDataQo
     * @return
     */
    List<DailyData> dailyData(DailyDataQo dailyDataQo);

    /**
     * 最近（几天内放量的股票）
     * @return
     */
    List<String> heavenVolumeRatio(@Param("startDate") LocalDate startDate,
                                   @Param("endDate") LocalDate endDate,
                                   @Param("minVolRatio") BigDecimal minVolRatio,
                                   @Nullable @Param("codes") List<String> codes);
}
