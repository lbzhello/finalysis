package xyz.liujin.finalysis.base.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.base.executor.TaskPool;

import java.util.concurrent.ExecutorService;

@Api("运行状态监控")
@RestController
@RequestMapping("monitor")
public class MonitorController {
    @ApiOperation("获取线程池信息")
    @GetMapping("task-pool")
    public Mono<ExecutorService> taskPool() {
        return Mono.just(TaskPool.getInstance());
    }
}