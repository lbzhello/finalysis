package xyz.liujin.finalysis.analysis;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.Scheduled;
import xyz.liujin.finalysis.analysis.entity.AvgLine;
import xyz.liujin.finalysis.analysis.qo.AvgLineQo;
import xyz.liujin.finalysis.analysis.service.AvgLineService;

import java.time.LocalDate;

@Configuration
public class AnalysisApp {
    private static final Logger logger = LoggerFactory.getLogger(AnalysisApp.class);
    @Autowired
    private AvgLineService avgLineService;

    /**
     * 每日 6 点计算均线
     */
    @Scheduled(cron = "0 0 18 * * ?")
    public void refreshAvgLineDaily() {
        logger.debug("start refresh avg line daily {}", LocalDate.now());
        // 从数据库获取最新的日期
        LocalDate start = avgLineService.lambdaQuery()
                .orderByDesc(AvgLine::getDate)
                .oneOpt()
                .map(AvgLine::getDate)
                .orElse(LocalDate.now());

        logger.debug("latest date in db avg_line is {}", start);

        // 计算最新均线数据
        avgLineService.refreshAvgLine(AvgLineQo.builder()
                .startDate(start)
                .endDate(LocalDate.now())
                .build());
    }
}
