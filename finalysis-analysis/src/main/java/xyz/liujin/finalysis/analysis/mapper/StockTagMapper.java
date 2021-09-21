package xyz.liujin.finalysis.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.liujin.finalysis.analysis.entity.StockTag;

import java.time.LocalDate;

public interface StockTagMapper extends BaseMapper<StockTag> {
    void deleteByDateAndTag(@Param("date") LocalDate date, @Param("tag") String tag);
}
