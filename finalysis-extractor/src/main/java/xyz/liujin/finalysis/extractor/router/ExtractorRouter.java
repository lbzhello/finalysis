package xyz.liujin.finalysis.extractor.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import xyz.liujin.finalysis.extractor.controller.ExtractController;
import xyz.liujin.finalysis.extractor.handler.ExtractHandler;

/**
 * 此类仅用于测试 webflux 函数式编程测试
 * @deprecated 功能已经被 {@link ExtractController} 取代
 */
@Deprecated
@Configuration
public class ExtractorRouter {
    public static final Logger logger = LoggerFactory.getLogger(ExtractorRouter.class);

    @Bean
    public RouterFunction<ServerResponse> extractStock(ExtractHandler extractHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/test/extract/stock"), extractHandler::extractStock)
                .andRoute(RequestPredicates.GET("/test/extract/k"), extractHandler::extractKLine)
                ;
    }
}
