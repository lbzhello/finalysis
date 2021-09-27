package xyz.liujin.finalysis.daily;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import xyz.liujin.finalysis.daily.service.KLineService;

import java.time.LocalDate;

/**
 * daily 模块统一功能接口
 */
@Configuration
public class DailyApp {
    @Autowired
    private KLineService kLineService;

    /**
     * 获取最新的数据日期
     * @return
     */
    public LocalDate getLatestDate() {
        return kLineService.getLatestDate();
    }
}
