package xyz.liujin.finalysis.analysis.strategy;

import cn.hutool.core.collection.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.SustainHighVolQo;
import xyz.liujin.finalysis.analysis.dto.SustainHighVolReq;
import xyz.liujin.finalysis.analysis.mapper.AnalysisMapper;
import xyz.liujin.finalysis.analysis.resp.SustainHighVolDto;
import xyz.liujin.finalysis.analysis.service.AnalysisService;
import xyz.liujin.finalysis.base.page.PageQo;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.service.KLineService;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 持续放量指标
 */
@Service
public class SustainHighVolStrategy {
    private static final Logger logger = LoggerFactory.getLogger(SustainHighVolStrategy.class);

    @Autowired
    private AnalysisService analysisService;

    @Autowired
    private KLineService kLineService;

    @Autowired
    private AnalysisMapper analysisMapper;

    /**
     * 获取持续放量的股票
     * @param sustainHighVolReq
     * @return
     */
    public Flux<String> findCodes(SustainHighVolReq sustainHighVolReq) {

        SustainHighVolReq req = SustainHighVolReq.builder()
                // 默认数据库最新日期
                .date(ObjectUtils.firstNonNull(sustainHighVolReq.getDate(), kLineService.getLatestDate(), LocalDate.now()))
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

        List<SustainHighVolDto> sustainHighVolDtos = analysisMapper.sustainHighVol(SustainHighVolQo.builder()
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

        return Flux.fromIterable(sustainHighVolDtos)
                .filter(sustainHighVolDto -> {
                    BigDecimal minRatio = ObjectUtils.firstNonNull(req.getMinRatio(), BigDecimal.ZERO);
                    BigDecimal ratio = ObjectUtils.firstNonNull(sustainHighVolDto.getRatio(), BigDecimal.ZERO);
                    return ratio.compareTo(minRatio) >= 0;
                })
                .map(SustainHighVolDto::getStockCode);
    }
}
