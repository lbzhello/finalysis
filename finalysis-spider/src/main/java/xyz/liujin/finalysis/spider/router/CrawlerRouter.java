package xyz.liujin.finalysis.spider.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import xyz.liujin.finalysis.spider.handler.CrawlerHandler;

/**
 * 此类仅用于测试 webflux 函数式编程测试
 * @deprecated 功能已经被 {@link xyz.liujin.finalysis.spider.controller.CrawlerController} 取代
 */
@Deprecated
@Configuration
public class CrawlerRouter {
    public static final Logger logger = LoggerFactory.getLogger(CrawlerRouter.class);

    @Bean
    public RouterFunction<ServerResponse> crawlStock(CrawlerHandler crawlerHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/test/crawl/stock"), crawlerHandler::crawlStock)
                .andRoute(RequestPredicates.GET("/test/crawl/k"), crawlerHandler::crawlKLine)
                ;
    }
}
