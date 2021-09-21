package xyz.liujin.finalysis.analysis.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.IncreaseRatioQo;
import xyz.liujin.finalysis.analysis.mapper.IncreaseRatioMapper;

/**
 * 增幅比值指标
 * 最近 recDays 天增幅，与过去 hisDays 天增幅的比值
 * 数值大，说明最近几天股票增幅巨大，过去几天涨幅较小，龙抬头
 * 说明股票可能有重大利好消息
 */
@Service
public class IncreaseRatioStrategy implements Strategy<IncreaseRatioQo> {

    @Autowired
    private IncreaseRatioMapper increaseRatioMapper;

    @Override
    public Flux<String> findCodes(IncreaseRatioQo increaseRatioQo) {
        return Flux.fromIterable(increaseRatioMapper.findCodes(increaseRatioQo));
    }
}
