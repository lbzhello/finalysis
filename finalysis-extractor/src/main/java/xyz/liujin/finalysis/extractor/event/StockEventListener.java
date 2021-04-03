package xyz.liujin.finalysis.extractor.event;

import cn.hutool.core.collection.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import xyz.liujin.finalysis.extractor.constant.StockConst;
import xyz.liujin.finalysis.extractor.manager.ExtractManager;
import xyz.liujin.finalysis.stock.event.StockChangeEvent;

import java.time.LocalDate;
import java.util.Objects;

@Configuration
public class StockEventListener {
    private static Logger logger = LoggerFactory.getLogger(StockEventListener.class);

    @Autowired
    private ExtractManager extractManager;

    @Async
    @EventListener
    public void stockRefresh(StockChangeEvent event) {
        if (Objects.nonNull(event) && CollectionUtil.isNotEmpty(event.getAddCodes())) {
            logger.debug("found stock added: {}", event);
            extractManager.refreshKLine(StockConst.CN_FOUND_DATE, LocalDate.now(), event.getAddCodes());
        }
    }
}
