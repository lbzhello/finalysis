package xyz.liujin.finalysis.analysis.listener;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import xyz.liujin.finalysis.analysis.event.RecommendChangeEvent;

@Configuration
public class AnalysisEventListener {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisEventListener.class);

    @Async
    @EventListener
    public void recommendChange(RecommendChangeEvent event) {

    }

}
