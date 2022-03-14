package xyz.liujin.finalysis.base.util;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.text.CharSequenceUtil;
import okhttp3.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Flux;

import javax.annotation.Nullable;
import java.io.IOException;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

/**
 * http 请求反应式客户端
 * 基于 okhttp
 */
public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    // http client 可以多个共享，共享线程池
    private static OkHttpClient httpClient = new OkHttpClient.Builder()
            .connectTimeout(Duration.ofSeconds(15))//连接超时
            .writeTimeout(Duration.ofSeconds(10))//写超时，也就是请求超时
            .readTimeout(Duration.ofSeconds(10))//读取超时
            .callTimeout(Duration.ofSeconds(20))//调用超时，也是整个请求过程的超时
            .build();

    public static final RequestSpec get(String url) {
        return new GetRequestSpec(url);
    }

    public static final RequestSpec post(String url) {
        return new PostRequestSpec(url);
    }

    public static final RequestSpec postJSON(String url) {
        return new PostRequestSpec(url).contentType("application/json");
    }

    /**
     * 策略模式，不同请求方法（GET, POST, PUT, DELETE, PATCH）对应不同实现类
     */
    public static class GetRequestSpec extends RequestSpec {
        public GetRequestSpec(String uri) {
            super(uri);
        }

        @Override
        public Request create() {
            return builder()
                    .get()
                    .build();
        }
    }

    public static class PostRequestSpec extends RequestSpec {
        public PostRequestSpec(String uri) {
            super(uri);
        }

        @Override
        public Request create() {
            return builder()
                    .post(buildBody())
                    .build();
        }
    }

    public static class PutRequestSpec extends RequestSpec {
        public PutRequestSpec(String uri) {
            super(uri);
        }

        @Override
        public Request create() {
            return builder()
                    .put(buildBody())
                    .build();
        }
    }

    public static class DeleteRequestSpec extends RequestSpec {
        public DeleteRequestSpec(String uri) {
            super(uri);
        }

        @Override
        public Request create() {
            return builder()
                    .delete(buildBody())
                    .build();
        }
    }

    public static class PatchRequestSpec extends RequestSpec {
        public PatchRequestSpec(String uri) {
            super(uri);
        }

        @Override
        public Request create() {
            return builder()
                    .patch(buildBody())
                    .build();
        }
    }

    // 请求规格，用来限定接口方法
    public static class RequestSpec {
        private String uri = "";
        // 请求路径，和 uri 拼接组成完整的 url
        private String path = "";
        private String contentType = "*/*";
        private Map<String, Object> headers = new HashMap<>();

        private String body;

        public RequestSpec(String uri) {
            this.uri = uri;
        }

        /**
         * 模板方法，创建 okhttp 请求对象
         * @see #builder()
         * @return
         */
        public Request create() {
            return builder()
                    .get() // 默认 GET 请求
                    .build();
        }

        /**
         * 提供给子类调用，可以根据需要添加自定义配置
         * 一般情况下不要重写此方法
         * @see #create()
         * @return
         */
        protected Request.Builder builder() {
            return new Request.Builder()
                    .url(buildUrl())
                    .headers(buildHeaders());
        }

        // 构建完整请求路径
        protected String buildUrl() {
            String url = this.uri == null ? "" : this.uri;
            if (CharSequenceUtil.isNotBlank(this.path)) {
                // 拼接路径
                url = url.endsWith("/") ? url.substring(0, url.length() - 1) : url;

                String p = this.path;
                p = p.startsWith("/") ? p : "/" + p;

                url = url + p;
            }
            return url;
        }

        // 构建请求头
        protected Headers buildHeaders() {
            Headers.Builder builder = new Headers.Builder();
            // 配置请求头
            if (CharSequenceUtil.isNotBlank(contentType)) {
                builder.set("Content-Type", contentType);
            }
            if (MapUtil.isNotEmpty(headers)) {
                Flux.fromIterable(headers.entrySet())
                        .subscribe(entry -> {
                            builder.set(entry.getKey(), String.valueOf(entry.getValue()));
                        });
            }

            return builder.build();
        }

        // 构建请求体
        protected @Nullable RequestBody buildBody() {
            RequestBody body0 = null;
            if (this.body != null) {
                body0 = RequestBody.create(MediaType.parse(this.contentType), this.body);
            }
            return body0;
        }

        /**
         * 发起异步请求
         * 注意使用后 ResponseBody 需要关闭
         * @return
         */
        public Flux<Response> req() {
            return Flux.just(create())
                    .map(httpClient::newCall)
                    .flatMap(call -> Flux.create(fluxSink -> {
                        call.enqueue(new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                fluxSink.error(e);
                                fluxSink.complete();
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                fluxSink.next(response);
                                fluxSink.complete();
                            }
                        });
                    }));
        }

        /**
         * 获取请求体
         * @return
         */
        public Flux<String> reqBody() {
            return req().map(response -> {
                try {
                    try (ResponseBody responseBody = response.body()) {
                        if (Objects.nonNull(responseBody)) {
                            return responseBody.string();
                        }
                    }
                } catch (Exception e) {
                    logger.error("failed to get body str {}", response.toString());
                }
                return "";
            });
        }

        public RequestSpec path(String path) {
            this.path = path;
            return this;
        }

        public RequestSpec contentType(String contentType) {
            this.contentType = contentType;
            return this;
        }

        public RequestSpec header(String name, Object value) {
            this.headers.put(name, String.valueOf(value));
            return this;
        }

        public RequestSpec body(String body) {
            this.body = body;
            return this;
        }
    }
}
