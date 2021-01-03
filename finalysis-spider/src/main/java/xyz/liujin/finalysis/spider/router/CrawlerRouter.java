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

@Configuration
public class CrawlerRouter {
    public static final Logger logger = LoggerFactory.getLogger(CrawlerRouter.class);

    @Bean
    public RouterFunction<ServerResponse> crawlStock(CrawlerHandler crawlerHandler) {
        return RouterFunctions
                .route(RequestPredicates.GET("/crawl/stock"), crawlerHandler::crawStock)
                .andRoute(RequestPredicates.GET("/crawl/k"), crawlerHandler::crawKLine)
                .andRoute(RequestPredicates.GET("/crawl/k/{stockCode}"), crawlerHandler::crawKLineByCode);
    }
}
