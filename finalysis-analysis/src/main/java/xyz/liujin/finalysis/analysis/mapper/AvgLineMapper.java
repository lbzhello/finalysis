package xyz.liujin.finalysis.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xyz.liujin.finalysis.analysis.entity.AvgLine;

import java.time.LocalDate;

public interface AvgLineMapper extends BaseMapper<AvgLine> {
    /**
     * 获取数据库最新数据的日期
     * @return
     */
    LocalDate getLatestDate();
}
