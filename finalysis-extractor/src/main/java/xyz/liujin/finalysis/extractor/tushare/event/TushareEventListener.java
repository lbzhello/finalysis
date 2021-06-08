package xyz.liujin.finalysis.extractor.tushare.event;

import cn.hutool.core.collection.CollectionUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.base.constant.StockConst;
import xyz.liujin.finalysis.base.executor.TaskPool;
import xyz.liujin.finalysis.extractor.tushare.manager.TushareManager;
import xyz.liujin.finalysis.stock.event.StockChangeEvent;

import java.time.LocalDate;
import java.util.Objects;

@Configuration
public class TushareEventListener {
    private static Logger logger = LoggerFactory.getLogger(TushareEventListener.class);

    @Autowired
    private TushareManager tushareManager;

    @Async
    @EventListener
    public void stockRefresh(StockChangeEvent event) {
        if (Objects.nonNull(event) && CollectionUtil.isNotEmpty(event.getAddCodes())) {
            logger.debug("found stock added: {}", event);
            // 更新 K 线数据
            tushareManager.refreshKLine(StockConst.CN_FOUND_DATE, LocalDate.now(), event.getAddCodes())
                    .publishOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                    .subscribe();
            // 更新股票日指标数据
            tushareManager.refreshDailyIndicator(StockConst.CN_FOUND_DATE, LocalDate.now(), event.getAddCodes())
                    .publishOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                    .subscribe();
        }
    }
}
