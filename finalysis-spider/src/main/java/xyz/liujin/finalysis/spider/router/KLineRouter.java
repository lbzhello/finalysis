package xyz.liujin.finalysis.spider.router;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import xyz.liujin.finalysis.spider.handler.KLineHandler;

@Configuration
public class KLineRouter {
    @Bean
    public RouterFunction<ServerResponse> kline(KLineHandler kLineHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/k-line/add-one"), kLineHandler::getByCode);
    }

    @Bean
    public RouterFunction<ServerResponse> queryAllKLine(KLineHandler kLineHandler) {
        return RouterFunctions.route(RequestPredicates.GET("/k-line/query-all"), kLineHandler::queryAll);
    }
}
