package xyz.liujin.finalysis.analysis.strategy;

import cn.hutool.core.collection.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.dto.SustainHighAmountQo;
import xyz.liujin.finalysis.analysis.dto.SustainHighAmountReq;
import xyz.liujin.finalysis.analysis.mapper.AnalysisMapper;
import xyz.liujin.finalysis.analysis.resp.SustainHighVolDto;
import xyz.liujin.finalysis.analysis.service.AnalysisService;
import xyz.liujin.finalysis.base.page.PageQo;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.service.KLineService;

import java.time.LocalDate;
import java.util.List;

/**
 * 成交额策略
 * 最近 n 天成交额与过去 m 天成交额比值
 * 最近几天平均成交额远远大于过去几天平均成交额
 * 说明股票开始异动，往往是启动（向上或向下）的标识
 */
@Service
public class SustainHighAmountStrategy extends QueryStrategy<SustainHighAmountReq> {
    private static final Logger logger = LoggerFactory.getLogger(SustainHighAmountStrategy.class);

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private KLineService kLineService;

    @Autowired
    private AnalysisMapper analysisMapper;

    @Override
    public SustainHighAmountReq getQueryStrategy(ScoreQo scoreQo) {
        return scoreQo.getSustainHighAmount();
    }

    /**
     * 获取持续放量的股票
     * @param sustainHighAmountReq
     * @return
     */
    @Override
    public Flux<String> findCodes(SustainHighAmountReq sustainHighAmountReq) {

        SustainHighAmountReq req = SustainHighAmountReq.builder()
                // 默认数据库最新日期
                .date(ObjectUtils.firstNonNull(sustainHighAmountReq.getDate(), kLineService.getLatestDate(), LocalDate.now()))
                .build();

        // 总共需要统计的天数
        int days = req.getRecentDays() + req.getHistoryDays();

        List<LocalDate> calendars = kLineService.tradingCalendar(req.getDate(), days);
        if (CollectionUtil.isEmpty(calendars) || calendars.size() != days) {
            logger.error("日历交易天数和统计天数不一致");
            return Flux.empty();
        }

        // 最近量比统计区间
        LocalDate recEnd = calendars.get(0);
        LocalDate recStart = calendars.get(req.getRecentDays() -1);

        // 历史量比统计区间
        LocalDate hisEnd = calendars.get(req.getRecentDays());
        LocalDate hisStart = calendars.get(days -1);

        List<SustainHighVolDto> sustainHighVolDtos = analysisMapper.sustainHighVol(SustainHighAmountQo.builder()
                .codes(req.getCodes())
                .minRatio(req.getMinRatio())
                .recentStartDate(recStart)
                .recentEndDate(recEnd)
                .historyStartDate(hisStart)
                .historyEndDate(hisEnd)
                .page(PageQo.builder()
                        .orderBy("ratio desc")
                        .build())
                .build());

        logger.debug("sustainHighVol total {}", sustainHighVolDtos.size());

        return Flux.fromIterable(sustainHighVolDtos)
                .map(SustainHighVolDto::getStockCode);
    }
}
