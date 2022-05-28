package xyz.liujin.finalysis.start.client;

import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;

public class GreetingWebClient {
    private WebClient client = WebClient.create("http://localhost:8080");

    public static final String USER_AGENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/86.0.4240.193 Safari/537.36";

    public String getResult() {
        return ">> result = " + client.get()
                .uri("/hello")
                .accept(MediaType.TEXT_PLAIN)
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    public void request() {
        WebClient.create().get().uri("http://www.baidu.com")
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(it ->{
                    System.out.println(it);
                });

        WebClient.create().post().uri("/api/search/secCheck?random=0.8018927628913926")
                .header(HttpHeaders.USER_AGENT, USER_AGENT)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromValue("keyword=300124"))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(it -> {
                    System.out.println(it);
                });
    }

    public static void main(String[] args) {
        WebClient.create().post().uri("http://www.szse.cn/api/search/secCheck?random=0.8018927628913926")
                .header(HttpHeaders.USER_AGENT, USER_AGENT)
                .header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body(BodyInserters.fromValue("keyword=300124"))
                .retrieve()
                .bodyToMono(String.class)
                .subscribe(it -> {
                    System.out.println("hello");
                    System.out.println(it);
                });
    }

}
