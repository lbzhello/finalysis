package xyz.liujin.finalysis.analysis.strategy;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.IncreaseRatioQo;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.mapper.IncreaseRatioMapper;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.DailyApp;

import java.time.LocalDate;

/**
 * 增幅比值指标
 * 最近 recDays 天增幅，与过去 hisDays 天增幅的比值
 * 数值大，说明最近几天股票增幅巨大，过去几天涨幅较小，龙抬头
 * 说明股票可能有重大利好消息
 */
@Service
public class IncreaseRatioStrategy extends ScoreStrategy<IncreaseRatioQo> {
    private static final MyLogger logger = MyLogger.getLogger(IncreaseRatioStrategy.class);

    @Autowired
    private IncreaseRatioMapper increaseRatioMapper;

    @Autowired
    private DailyApp dailyApp;

    @Override
    public IncreaseRatioQo getQueryStrategy(ScoreQo scoreQo) {
        return scoreQo.getIncreaseRatio();
    }

    @Override
    public Flux<String> findCodes(IncreaseRatioQo increaseRatioQo) {
        // 日期默认数据库最新
        increaseRatioQo.setDate(ObjectUtils.firstNonNull(increaseRatioQo.getDate(),
                dailyApp.getLatestDate(),
                LocalDate.now()));

        return Flux.fromIterable(increaseRatioMapper.findCodes(increaseRatioQo));
    }

}
