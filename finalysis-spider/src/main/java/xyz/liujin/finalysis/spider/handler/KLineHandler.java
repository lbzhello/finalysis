package xyz.liujin.finalysis.spider.handler;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.spider.entity.KLine;
import xyz.liujin.finalysis.spider.service.KLineService;

@Component
public class KLineHandler {
    public static final Logger logger = LoggerFactory.getLogger(KLineHandler.class);

    @Autowired
    private KLineService kLineService;

    public Mono<ServerResponse> query(ServerRequest serverRequest) {
        logger.debug("getOne {}", serverRequest);
        String code = serverRequest.pathVariable("code");
        KLine kLine = kLineService.getById(code);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(kLine == null ? "" : kLine));
    }

    public Mono<ServerResponse> page(ServerRequest serverRequest) {
        logger.debug("queryAll {}", serverRequest);
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(kLineService.list()));
    }

    public Mono<ServerResponse> hello(ServerRequest serverRequest) {
        return ServerResponse.ok().contentType(MediaType.APPLICATION_NDJSON)
                .body(Flux.create(sink -> {
                    sink.next("1234");
                    sink.next("1234");
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    sink.next("1234");
                    sink.complete();
                }), String.class);
    }
}
