package xyz.liujin.finalysis.spider.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

import java.time.Duration;

@RestController
@RequestMapping("/crawl")
public class CrawlController {
    @GetMapping("/interval")
    public Flux<String> stringFlux() {
        return Flux.interval(Duration.ofSeconds(1))
                .map(it -> "hello");
    }

    @GetMapping("/create")
    public Flux<String> create() {
        return Flux.create(sink -> {
            try {
                sink.next("23333");
                Thread.sleep(1000);
                sink.next("23334");
                Thread.sleep(1000);
                sink.next("23335");
                Thread.sleep(1000);
                sink.next("233346");
                Thread.sleep(1000);
                sink.next("233347");
                Thread.sleep(1000);
                sink.complete();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }

}
