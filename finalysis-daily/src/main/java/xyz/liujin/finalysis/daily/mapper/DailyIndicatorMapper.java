package xyz.liujin.finalysis.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xyz.liujin.finalysis.daily.entity.DailyIndicator;

import java.time.LocalDate;

public interface DailyIndicatorMapper extends BaseMapper<DailyIndicator> {
    /**
     * 获取数据库最新数据的日期
     * @return
     */
    LocalDate getLatestDate();

    /**
     * 批量保存均线数据；如果冲突 (code, date) 则更新
     * @param dailyIndicator
     */
    void insertOrUpdate(DailyIndicator dailyIndicator);

}
