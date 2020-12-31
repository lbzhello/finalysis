package xyz.liujin.finalysis.spider.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.*;
import xyz.liujin.finalysis.spider.handler.KLineHandler;
import xyz.liujin.finalysis.spider.service.KLineService;

@Configuration
public class KLineRouter {
    @Bean
    public RouterFunction<ServerResponse> kline(KLineHandler kLineHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/k-line/add-one"), kLineHandler::query);
    }
}
