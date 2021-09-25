package xyz.liujin.finalysis.analysis.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.liujin.finalysis.analysis.entity.StockScore;

import java.time.LocalDate;

public interface StockScoreMapper extends BaseMapper<StockScore> {
    void deleteByDateAndTag(@Param("date") LocalDate date, @Param("scoreCode") String scoreCode);
}
