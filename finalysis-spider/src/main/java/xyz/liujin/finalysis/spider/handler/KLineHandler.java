package xyz.liujin.finalysis.spider.handler;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.entity.KLine;
import xyz.liujin.finalysis.spider.service.KLineService;

@Component
public class KLineHandler {
    @Autowired
    private KLineService kLineService;

    public Mono<ServerResponse> query(ServerRequest serverRequest) {
        kLineService.add(KLineDto.builder().stockCode(10002).build());
        return ServerResponse.ok().contentType(MediaType.APPLICATION_JSON)
                .body(BodyInserters.fromValue(kLineService.getOne(10001)));
    }
}
