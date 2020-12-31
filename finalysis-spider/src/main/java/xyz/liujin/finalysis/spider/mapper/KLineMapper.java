package xyz.liujin.finalysis.spider.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import xyz.liujin.finalysis.spider.entity.KLine;

import java.util.List;

public interface KLineMapper extends BaseMapper<KLine> {
    List<KLine> findAll();
}
