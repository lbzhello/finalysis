package xyz.liujin.finalysis.analysis.mapper;


import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xyz.liujin.finalysis.analysis.entity.Recommend;

public interface RecommendMapper extends BaseMapper<Recommend> {
    /**
     * 保存更新荐股
     * @param recommend
     */
    void insertOrUpdate(Recommend recommend);

}
