package xyz.liujin.finalysis.start.handler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.server.ServerRequest;
import org.springframework.web.reactive.function.server.ServerResponse;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.common.constant.HttpHeaderValues;

@Component
public class GreetingHandler {

    public Mono<ServerResponse> hello(ServerRequest request) {

        WebClient.create().post().uri("http://www.szse.cn/api/search/secCheck?random=0.8018927628913926")
                .header(HttpHeaders.USER_AGENT, HttpHeaderValues.USER_AGENT)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromValue("keyword=300124"))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(it -> {
                    System.out.println("hello");
                    System.out.println(it);
                });
        return ServerResponse.ok().contentType(MediaType.TEXT_PLAIN)
                .body(BodyInserters.fromValue("Hello, Spring!"));
    }
}
