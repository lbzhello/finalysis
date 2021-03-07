package xyz.liujin.finalysis.analysis.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import xyz.liujin.finalysis.analysis.service.AvgLineService;
import xyz.liujin.finalysis.base.event.KLineRefreshEvent;

@Configuration
public class AnalysisEventListener {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisEventListener.class);

    @Autowired
    private AvgLineService avgLineService;

    /**
     * 收到 K 线变更事件，更新均线信息
     * @param kLineRefreshEvent
     */
    @Async
    @EventListener
    public void kLineRefresh(KLineRefreshEvent kLineRefreshEvent) {
        logger.debug("found k line refresh {}", kLineRefreshEvent);
        avgLineService.refreshAvgLine(kLineRefreshEvent.getStart(),
                kLineRefreshEvent.getEnd(),
                kLineRefreshEvent.getCodes());
    }
}
