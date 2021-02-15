package xyz.liujin.finalysis.common.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import xyz.liujin.finalysis.common.schedule.ThreadPool;

import java.util.concurrent.Executor;

@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        return ThreadPool.getInstance();
    }
}
