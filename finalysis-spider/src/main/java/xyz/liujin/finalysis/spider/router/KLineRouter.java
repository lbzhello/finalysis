package xyz.liujin.finalysis.spider.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import xyz.liujin.finalysis.spider.handler.KLineHandler;

import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;

import static org.springframework.web.reactive.function.server.RequestPredicates.GET;

@Configuration
public class KLineRouter {
    private Logger logger = LoggerFactory.getLogger(KLineRouter.class);

    @Bean
    public RouterFunction<ServerResponse> kline(KLineHandler kLineHandler) {
        logger.debug("create bean kline {}", kLineHandler.getClass());
        return RouterFunctions
                // 分页查询
                .route(GET("/k/page"), kLineHandler::page)
                .andRoute(GET("/k/{code}"), kLineHandler::page)
                // 分时
                .andRoute(GET("/k/{code}/today"), kLineHandler::query)
                // 日 K
                .andRoute(GET("/k/{code}/day"), kLineHandler::query)
                // 周 k
                .andRoute(GET("/k/{code}/week"), kLineHandler::query)
                // 月 k
                .andRoute(GET("/k/{code}/month"), kLineHandler::query)
                // 季 k
                .andRoute(GET("/k/{code}/quarter"), kLineHandler::query)
                // 年 k
                .andRoute(GET("/k/{code}/year"), kLineHandler::query)
                // 120 分 k
                .andRoute(GET("/k/{code}/120"), kLineHandler::query)
                // 60 分 k
                .andRoute(GET("/k/{code}/60"), kLineHandler::query)
                // 30 分 k
                .andRoute(GET("/k/{code}/30"), kLineHandler::query)
                // 15 分 k
                .andRoute(GET("/k/{code}/15"), kLineHandler::query)
                // 5 分 k
                .andRoute(GET("/k/{code}/5"), kLineHandler::query)
                // 1 分 K
                .andRoute(GET("/k/{code}/1"), kLineHandler::query)
                // hello
                .andRoute(GET("/k/{code}/hello"), kLineHandler::hello);
    }

}
