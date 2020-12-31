package xyz.liujin.finalysis.spider.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.spider.service.KLineService;

@Component
public class KLineHandler {
    public static final Logger logger = LoggerFactory.getLogger(KLineHandler.class);

    @Autowired
    private KLineService kLineService;

    public Mono<ServerResponse> getByCode(ServerRequest serverRequest) {
        logger.debug("getOne {}", serverRequest);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(kLineService.getById(10001)));
    }

    public Mono<ServerResponse> queryAll(ServerRequest serverRequest) {
        logger.debug("queryAll {}", serverRequest);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(kLineService.list()));
    }
}
