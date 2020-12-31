package xyz.liujin.finalysis.start.router;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.server.RequestPredicates;
import org.springframework.web.reactive.function.server.RouterFunction;
import org.springframework.web.reactive.function.server.RouterFunctions;
import org.springframework.web.reactive.function.server.ServerResponse;
import xyz.liujin.finalysis.start.handler.GreetingHandler;

@Configuration
public class GreetingRouter {
    private static final Logger logger = LoggerFactory.getLogger(GreetingRouter.class);

    @Bean
    public RouterFunction<ServerResponse> route(GreetingHandler greetingHandler) {
        logger.debug("create {}", GreetingRouter.class);

        return RouterFunctions.route(RequestPredicates.GET("/hello")
                .and(RequestPredicates.accept(MediaType.TEXT_PLAIN)), greetingHandler::hello);
    }
}
