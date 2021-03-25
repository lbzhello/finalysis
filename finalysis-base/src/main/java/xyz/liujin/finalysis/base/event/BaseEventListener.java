package xyz.liujin.finalysis.base.event;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import xyz.liujin.finalysis.base.service.KLineService;

@Configuration
public class BaseEventListener {
    Logger logger = LoggerFactory.getLogger(BaseEventListener.class);

    @Autowired
    private KLineService kLineService;

    @Async
    @EventListener
    public void klineChange(KLineChangeEvent kLineChangeEvent) {
        logger.debug("receive k-line change, update volume ratio");
        kLineService.calculateVolumeRatio(kLineChangeEvent.getStart(),
                kLineChangeEvent.getEnd(),
                kLineChangeEvent.getCodes());
    }
}
