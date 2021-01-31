package xyz.liujin.finalysis.common.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xyz.liujin.finalysis.common.entity.KLine;

import java.util.List;

public interface KLineMapper extends BaseMapper<KLine> {
    List<KLine> findAll();
}
