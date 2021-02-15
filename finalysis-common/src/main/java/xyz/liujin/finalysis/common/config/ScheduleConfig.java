package xyz.liujin.finalysis.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.TaskScheduler;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * {@link org.springframework.scheduling.annotation.Scheduled} 配置
 * Scheduled 默认单线程
 */
@EnableScheduling
@Configuration
public class ScheduleConfig {
    /**
     * 配置 {@link org.springframework.scheduling.annotation.Scheduled} 异步执行
     * @return
     */
    @Bean
    public TaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(100);
        taskScheduler.setThreadNamePrefix("finalysis-scheduler");
        return taskScheduler;
    }

}
