package xyz.liujin.finalysis.analysis.mapper;

import xyz.liujin.finalysis.analysis.dto.MinimumPriceSupportQo;

import java.util.List;

/**
 * 计分 Mapper 类，不必要新建查询类
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/10/7
 */
public interface ScoreStrategyMapper {
    List<String> minimumPriceSupport(MinimumPriceSupportQo qo);
}
