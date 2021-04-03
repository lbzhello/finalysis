package xyz.liujin.finalysis.extractor.tushare.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import xyz.liujin.finalysis.extractor.tushare.controller.TushareController;
import xyz.liujin.finalysis.extractor.tushare.handler.TushareHandler;

/**
 * 此类仅用于测试 webflux 函数式编程测试
 * @deprecated 功能已经被 {@link TushareController} 取代
 */
@Deprecated
@Configuration
public class TushareRouter {
    public static final Logger logger = LoggerFactory.getLogger(TushareRouter.class);

    @Bean
    public RouterFunction<ServerResponse> extractStock(TushareHandler tushareHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/test/extract/stock"), tushareHandler::extractStock)
                .andRoute(RequestPredicates.GET("/test/extract/k"), tushareHandler::extractKLine)
                ;
    }
}
