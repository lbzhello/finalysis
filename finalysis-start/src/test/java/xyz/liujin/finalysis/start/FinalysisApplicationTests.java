package xyz.liujin.finalysis.start;

import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import xyz.liujin.finalysis.common.constant.HttpHeaderValues;
import xyz.liujin.finalysis.spider.util.HttpUtils;

import java.io.BufferedReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;

//@RunWith(SpringRunner.class)
@SpringBootTest
class FinalysisApplicationTests {

	public static void main(String[] args) {
		WebClient.create().post().uri("http://www.szse.cn/api/search/secCheck?random=0.8018927628913926")
				.header(HttpHeaders.USER_AGENT, HttpHeaderValues.USER_AGENT)
				.header(HttpHeaders.CONTENT_TYPE, MediaType.APPLICATION_FORM_URLENCODED_VALUE)
				.body(BodyInserters.fromValue("keyword=300124"))
				.retrieve()
				.bodyToMono(String.class)
				.subscribe(it -> {
					System.out.println("hello");
					System.out.println(it);
				}, e -> {
					System.out.println(e.getMessage());
				});
	}

	@Test
	void contextLoads() {
	}

}
