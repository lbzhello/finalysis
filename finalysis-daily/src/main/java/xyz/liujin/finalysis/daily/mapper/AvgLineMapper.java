package xyz.liujin.finalysis.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
import org.springframework.lang.Nullable;
import xyz.liujin.finalysis.daily.entity.AvgLine;
import xyz.liujin.finalysis.daily.entity.DayAvgLine;
import xyz.liujin.finalysis.daily.qo.AvgLineQo;

import java.time.LocalDate;
import java.util.List;

public interface AvgLineMapper extends BaseMapper<AvgLine> {
    /**
     * 获取数据库最新数据的日期
     * @return
     */
    LocalDate getLatestDate();

    /**
     * 批量保存均线数据；如果冲突 (code, date) 则更新
     * @param avgLines
     */
    void saveBatchByCodeDate(@Param("avgLines") List<DayAvgLine> avgLines);

    /**
     * 批量保存均线数据；如果冲突 (code, date, days) 则更新
     * @param avgLines
     * @deprecated {@link #saveBatchByCodeDate(List)}
     */
    @Deprecated
    void saveBatchByCodeDateStatistic(@Param("avgLines") List<AvgLine> avgLines);

    /**
     * 查询日均线数据
     * @return
     */
    List<DayAvgLine> findDayAvg(AvgLineQo avgLineQo);

    List<String> trend5Up10(@NonNull @Param("start") LocalDate start,
                            @NonNull @Param("end") LocalDate end,
                            @Nullable @Param("codes") List<String> codes);
}
