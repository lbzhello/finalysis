package xyz.liujin.finalysis.daily.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Param;
import xyz.liujin.finalysis.daily.entity.KLine;
import xyz.liujin.finalysis.daily.qo.KLineQo;

import java.time.LocalDate;
import java.util.List;

public interface KLineMapper extends BaseMapper<KLine> {
    List<LocalDate> tradingCalendar(@Param("endDate") LocalDate endDate,
                                    @Param("limit") Integer limit);

    List<KLine> findOne();

    /**
     * 分页查询 todo 分页功能暂未完成
     * @return
     */
    List<KLine> pageQuery(KLineQo qo);

    /**
     * 获取数据库最新 K 线日期
     * @return
     */
    LocalDate getLatestDate();

    /**
     * 保存或更新
     * @param kLine
     */
    void insertOrUpdate(KLine kLine);
}
