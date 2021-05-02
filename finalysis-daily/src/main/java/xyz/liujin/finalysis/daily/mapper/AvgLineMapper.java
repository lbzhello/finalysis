package xyz.liujin.finalysis.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
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
     * 批量保存均线数据；如果冲突 (code, date, days) 则更新
     * @param avgLines
     * @deprecated {@link #saveBatchByCodeDate(List)}
     */
    void saveBatchByCodeDateStatistic(@Param("avgLines") List<AvgLine> avgLines);

    /**
     * 批量保存均线数据；如果冲突 (code, date) 则更新
     * @param avgLines
     */
    void saveBatchByCodeDate(@Param("avgLines") List<DayAvgLine> avgLines);

    /**
     * 查询日均线数据
     * @return
     */
    List<DayAvgLine> findDayAvg(AvgLineQo avgLineQo);

    /**
     * 获取股票趋势
     * @param start 趋势开始日期
     * @param end 结束日期
     * @param highStats 较高的统计类型
     * @param lowStats 较低的统计类型
     * @return
     * @deprecated 使用 {@link #trend5Up10(LocalDate, LocalDate)} 替换
     *             数据结构改变，不在从 avg_line 表获取数据
     *             改从 v_avg_line 获取数据
     */
    List<String> trend(@NonNull @Param("start") LocalDate start,
                       @NonNull @Param("end") LocalDate end,
                       @Param("highStats") int highStats,
                       @Param("lowStats") int lowStats);

    List<String> trend5Up10(@NonNull @Param("start") LocalDate start,
                       @NonNull @Param("end") LocalDate end);
}
