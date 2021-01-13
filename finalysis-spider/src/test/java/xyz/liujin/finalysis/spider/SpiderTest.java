package xyz.liujin.finalysis.spider;

import okhttp3.*;
import org.junit.Test;
import org.springframework.lang.NonNull;
import xyz.liujin.finalysis.spider.szse.SzseConst;

import java.io.IOException;
import java.time.Duration;
import java.time.OffsetDateTime;

public class SpiderTest {
    public static void main(String[] args) {
        System.out.println(OffsetDateTime.parse("2020-12-31"));
    }

    @Test
    public void commonTest() {
        System.out.println("hello");
    }


    public static void get() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(15))//连接超时
                .writeTimeout(Duration.ofSeconds(10))//写超时，也就是请求超时
                .readTimeout(Duration.ofSeconds(10))//读取超时
                .callTimeout(Duration.ofSeconds(20))//调用超时，也是整个请求过程的超时
                .build();
        Request request = new Request.Builder()
                .url("http://www.baidu.com")
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
                System.out.println(body);
            }
        });
    }

    public static void post() {
        OkHttpClient httpClient = new OkHttpClient.Builder()
                .connectTimeout(Duration.ofSeconds(10))//连接超时
                .writeTimeout(Duration.ofSeconds(5))//写超时，也就是请求超时
                .readTimeout(Duration.ofSeconds(5))//读取超时
                .callTimeout(Duration.ofSeconds(15))//调用超时，也是整个请求过程的超时
                .build();
        // application/octet-stream
        //application/x-www-form-urlencoded
        RequestBody body = RequestBody.create(MediaType.parse("application/x-www-form-urlencoded"), "keyword=300124");
        Request request = new Request.Builder()
                .post(body)
//                .url("https://www.baidu.com")
                .url(SzseConst.POST_SEC_CHECK)
                .build();
        Call call = httpClient.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(@NonNull Call call, @NonNull IOException e) {
                System.out.println(e.getMessage());
            }

            @Override
            public void onResponse(@NonNull Call call, @NonNull Response response) throws IOException {
                String body = response.body().string();
//                String body = new String(bytes, 0, bytes.length, "utf-8");
                System.out.println(body);
            }
        });
    }
}
