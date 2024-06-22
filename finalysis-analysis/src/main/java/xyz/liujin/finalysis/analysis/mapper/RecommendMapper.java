package xyz.liujin.finalysis.analysis.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xyz.liujin.finalysis.analysis.entity.Recommend;

import java.time.LocalDate;

public interface RecommendMapper extends BaseMapper<Recommend> {
    /**
     * 保存更新荐股
     *
     * @param recommend
     * @return
     */
    boolean insertOrUpdate(Recommend recommend);

    /**
     * 保留每日量额前 500 条数据
     * @param date
     */
    int retainRecommend500(LocalDate date);
}
