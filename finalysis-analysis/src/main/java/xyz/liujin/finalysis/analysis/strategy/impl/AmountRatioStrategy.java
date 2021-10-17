package xyz.liujin.finalysis.analysis.strategy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.AmountRatioQo;
import xyz.liujin.finalysis.analysis.mapper.ScoreStrategyMapper;
import xyz.liujin.finalysis.analysis.strategy.ScoreStrategy;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.daily.service.DailyService;

import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * 成交额比指标，最近几日平均成交额，与过去几日平均成交额比值；
 * 成交额比大，说明股票开始放量，持续放量说明股价可能会有一波行情
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/27
 */
@Service
public class AmountRatioStrategy extends ScoreStrategy<AmountRatioQo> {
    private static final MyLogger logger = MyLogger.getLogger(AmountRatioStrategy.class);

    @Autowired
    private DailyService dailyService;

    @Autowired
    private ScoreStrategyMapper scoreStrategyMapper;

    @Override
    public Flux<String> findCodes(AmountRatioQo amountRatioQo) {
        if (Objects.isNull(amountRatioQo.getDate())) {
            LocalDate date = dailyService.getLatestDateOrNow(amountRatioQo.getDate());
            amountRatioQo.setDate(date);
        }
        List<String> list = scoreStrategyMapper.amountRatio(amountRatioQo);
        return Flux.fromIterable(list);
    }

}
