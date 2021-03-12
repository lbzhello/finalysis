package xyz.liujin.finalysis.spider.event;

import cn.hutool.core.collection.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import xyz.liujin.finalysis.base.event.StockRefreshEvent;
import xyz.liujin.finalysis.spider.constant.StockConst;
import xyz.liujin.finalysis.spider.manager.CrawlManager;

import java.time.LocalDate;
import java.util.Objects;

@Configuration
public class StockEventListener {
    private static Logger logger = LoggerFactory.getLogger(StockEventListener.class);

    @Autowired
    private CrawlManager crawlManager;

    @Async
    @EventListener
    public void stockRefresh(StockRefreshEvent event) {
        logger.debug("found stock refresh: {}", event);
        if (Objects.nonNull(event) && CollectionUtil.isNotEmpty(event.getAddCodes())) {
            crawlManager.refreshKLine(StockConst.CN_FOUND_DATE, LocalDate.now(), event.getAddCodes());
        }
    }
}
