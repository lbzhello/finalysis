package xyz.liujin.finalysis.spider;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import xyz.liujin.finalysis.common.constant.HttpHeaderValues;
import xyz.liujin.finalysis.common.constant.HttpHeaders;
import xyz.liujin.finalysis.spider.constant.SzsePaths;
import xyz.liujin.finalysis.spider.util.HttpUtils;

import java.io.IOException;
import java.time.LocalDateTime;
import java.time.OffsetDateTime;
import java.time.format.DateTimeFormatter;

public class SpiderApplication {
    private static Logger logger = LoggerFactory.getLogger(SpiderApplication.class);
    public static void main(String[] args) {
        logger.info("汇川");
        HttpUtils.get("http://www.baidu.com").subscribe(response -> {
            try {
                System.out.println(response.body().string());
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        HttpUtils.post(SzsePaths.POST_SEC_CHECK)
                .contentType("application/x-www-form-urlencoded")
                .header(HttpHeaders.USER_AGENT, HttpHeaderValues.USER_AGENT)
                .body("keyword=300124")
                .subscribe(response -> {
                    try {
                        String body = response.body().string();
                        System.out.println(body);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
