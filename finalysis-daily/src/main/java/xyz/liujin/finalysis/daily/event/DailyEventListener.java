package xyz.liujin.finalysis.daily.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import xyz.liujin.finalysis.daily.service.AvgLineService;
import xyz.liujin.finalysis.daily.service.KLineService;


@Configuration
public class DailyEventListener {
    Logger logger = LoggerFactory.getLogger(DailyEventListener.class);

    @Autowired
    private KLineService kLineService;

    @Autowired
    private AvgLineService avgLineService;

    @Async
    @EventListener
    public void klineChange(KLineChangeEvent kLineChangeEvent) {
        logger.debug("receive k line change {}", kLineChangeEvent);

        // 更新量比数据
        logger.debug("start to calculate volume ratio");
        kLineService.calculateVolumeRatio(kLineChangeEvent.getStart(),
                kLineChangeEvent.getEnd(),
                kLineChangeEvent.getCodes());

        // 更新均线信息
        logger.debug("start to refresh avg line");
        avgLineService.refreshAvgLine(kLineChangeEvent.getStart(),
                kLineChangeEvent.getEnd(),
                kLineChangeEvent.getCodes());
    }


}
