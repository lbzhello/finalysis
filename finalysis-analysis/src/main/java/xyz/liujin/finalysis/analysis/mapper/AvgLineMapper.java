package xyz.liujin.finalysis.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import org.springframework.lang.NonNull;
import xyz.liujin.finalysis.analysis.dto.DayAvgLine;
import xyz.liujin.finalysis.analysis.entity.AvgLine;
import xyz.liujin.finalysis.analysis.qo.AvgLineQo;

import java.time.LocalDate;
import java.util.List;

public interface AvgLineMapper extends BaseMapper<AvgLine> {
    /**
     * 获取数据库最新数据的日期
     * @return
     */
    LocalDate getLatestDate();

    /**
     * 批量保存均线数据；如果冲突 (code, date, count) 则更新
     * @param avgLines
     */
    void saveBatchByCodeDateStatistic(List<AvgLine> avgLines);

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
     */
    List<String> trend(@NonNull @Param("start") LocalDate start,
                       @NonNull @Param("end") LocalDate end,
                       @Param("highStats") int highStats,
                       @Param("lowStats") int lowStats);
}
