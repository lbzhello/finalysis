package xyz.liujin.finalysis.analysis.strategy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.MinimumPriceSupportQo;
import xyz.liujin.finalysis.analysis.mapper.ScoreStrategyMapper;
import xyz.liujin.finalysis.analysis.score.ScoreType;
import xyz.liujin.finalysis.analysis.strategy.ScoreStrategy;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.daily.service.DailyService;

/**
 * 根据股价走势（K 线）选股
 * 比如：
 * 连续 3 日不破低点，说明股价经过一段时间的下跌，趋于稳定，或者突破后，经过一段时间吸盘，价格找到了支撑
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/27
 */
@Component(ScoreType.MINIMUM_PRICE_SUPPORT)
public class MinimumPriceSupportStrategy extends ScoreStrategy<MinimumPriceSupportQo> {
    private static final MyLogger logger = MyLogger.getLogger(MinimumPriceSupportStrategy.class);

    @Autowired
    private ScoreStrategyMapper scoreStrategyMapper;

    @Autowired
    private DailyService dailyService;
    @Override
    public Flux<String> findCodes(MinimumPriceSupportQo minimumPriceSupportQo) {
        // 默认数据库最新日期
        minimumPriceSupportQo.setDate(dailyService.getLatestDateOrNow(minimumPriceSupportQo.getDate()));
        
        return Flux.fromIterable(scoreStrategyMapper.minimumPriceSupport(minimumPriceSupportQo));
    }

}
