package xyz.liujin.finalysis.analysis.strategy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.dto.TurnRatioQo;
import xyz.liujin.finalysis.analysis.mapper.TurnRatioMapper;
import xyz.liujin.finalysis.analysis.strategy.ScoreStrategy;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.service.DailyService;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 成交额策略
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/27
 */
public class AmountStrategy extends ScoreStrategy<TurnRatioQo> {
    private static final MyLogger logger = MyLogger.getLogger(AmountStrategy.class);

    @Autowired
    private TurnRatioMapper turnRatioMapper;

    @Autowired
    private DailyService dailyService;

    @Override
    public TurnRatioQo getScoreable(ScoreQo scoreQo) {
        return scoreQo.getTurnRatio();
    }

    @Override
    public Flux<String> findCodes(TurnRatioQo turnRatioQo) {
        // 默认数据库最新日期
        if (Objects.isNull(turnRatioQo.getDate())) {
            turnRatioQo.setDate(ObjectUtils.firstNonNull(dailyService.getLatestDate(), LocalDate.now()));
        }

        return Flux.fromIterable(turnRatioMapper.findCodes(turnRatioQo));
    }

}
