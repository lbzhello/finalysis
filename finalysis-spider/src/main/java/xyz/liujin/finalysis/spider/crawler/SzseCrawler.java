package xyz.liujin.finalysis.spider.crawler;

import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import xyz.liujin.finalysis.common.constant.HttpHeaders;
import xyz.liujin.finalysis.spider.constant.SzsePaths;
import xyz.liujin.finalysis.spider.util.HttpUtils;

import java.io.IOException;

@Component
public class SzseCrawler {
    public void updateDayKLine() {
        HttpUtils.get(SzsePaths.GET_HISTORY_DATA_OF_DAY)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED_VALUE)
                .body("")
                .subscribe(response -> {
                    try {
                        String body = response.body().string();
                        System.out.println();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
    }
}
