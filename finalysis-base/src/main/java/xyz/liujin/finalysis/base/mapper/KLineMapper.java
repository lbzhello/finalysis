package xyz.liujin.finalysis.base.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xyz.liujin.finalysis.base.entity.KLine;

import java.time.LocalDate;
import java.util.List;

public interface KLineMapper extends BaseMapper<KLine> {
    List<KLine> findOne();

    /**
     * 获取数据库最新 K 线日期
     * @return
     */
    LocalDate getLatestDate();
}
