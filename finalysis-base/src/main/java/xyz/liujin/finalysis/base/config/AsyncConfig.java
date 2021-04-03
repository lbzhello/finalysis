package xyz.liujin.finalysis.base.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import xyz.liujin.finalysis.base.executor.TaskPool;

import java.util.concurrent.Executor;

/**
 * 配置 {@link org.springframework.scheduling.annotation.Async} 线程池
 * Async 默认单线程
 */
@EnableAsync
@Configuration
public class AsyncConfig implements AsyncConfigurer {
    @Override
    public Executor getAsyncExecutor() {
        return TaskPool.getInstance();
    }
}
