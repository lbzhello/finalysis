package xyz.liujin.finalysis.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
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
     * 获取上升趋势的股票，即 5 日线大于 10 日线
     * @param start 趋势开始日期
     * @return
     */
    List<String> upwards(LocalDate start);
}
