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
import java.util.function.Consumer;

public class HttpUtils {
    private static final Logger logger = LoggerFactory.getLogger(HttpUtils.class);

    public static final String GET = "get";
    public static final String POST = "post";
    public static final String PUT = "put";
    public static final String DELETE = "delete";
    public static final String PATCH = "patch";

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

    public static class GetRequestSpec extends RequestSpec {
        public GetRequestSpec(String uri) {
            super(uri);
        }

        @Override
        public Request create() {
            return super.builder()
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
            return super.builder()
                    .post(getBody())
                    .build();
        }
    }

    public static class PutRequestSpec extends RequestSpec {
        public PutRequestSpec(String uri) {
            super(uri);
        }

        @Override
        public Request create() {
            return super.builder()
                    .put(getBody())
                    .build();
        }
    }

    public static class DeleteRequestSpec extends RequestSpec {
        public DeleteRequestSpec(String uri) {
            super(uri);
        }

        @Override
        public Request create() {
            return super.builder()
                    .delete(getBody())
                    .build();
        }
    }

    public static class PatchRequestSpec extends RequestSpec {
        public PatchRequestSpec(String uri) {
            super(uri);
        }

        @Override
        public Request create() {
            return super.builder()
                    .patch(getBody())
                    .build();
        }
    }

    // 请求规格，用来限定接口方法
    public static class RequestSpec {
        private Consumer<IOException> onFailure = e -> System.out.println(e);
        private Consumer<Response> onResponse;

        private Request.Builder requestBuilder;

        private String httpMethod;

        private String uri = "";
        private String path = "";
        private String contentType = "*/*";
        private Map<String, Object> headers = new HashMap<>();

        private String body;

        public RequestSpec(String uri) {
            this.uri = uri;
        }

        /**
         * 模板方法，创建 okhttp 请求对象, 子类一般重写此方法创建请求对象
         * @see #builder()
         * @return
         */
        public Request create() {
            return builder()
                    .build();
        }

        /**
         * builder 暴露给子类，可以根据需要添加自定义配置
         * 子类一般不要重写此方法，通过重写 {@link #create()} 实现
         * @see #create()
         * @return
         */
        protected Request.Builder builder() {
            Request.Builder builder = new Request.Builder()
                    .url(getFullUrl());

            // 配置请求头
            if (CharSequenceUtil.isNotBlank(contentType)) {
                builder.header("Content-Type", contentType);
            }
            if (MapUtil.isNotEmpty(headers)) {
                Flux.fromIterable(headers.entrySet())
                        .subscribe(entry -> {
                            builder.header(entry.getKey(), String.valueOf(entry.getValue()));
                        });
            }
            return builder;
        }

        /**
         * 请求路径，和 uri 拼接组成完整的 url
         *
         * @param path
         * @return
         */
        public RequestSpec path(String path) {
            this.path = path;
            return this;
        }

        /**
         * http 文本类型
         *
         * @param contentType
         * @return
         */
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

        /**
         * 转成 flux 异步流
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

        // 获取完整请求路径
        protected String getFullUrl() {
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

        // 构建请求体
        protected @Nullable RequestBody getBody() {
            RequestBody body0 = null;
            if (this.body != null) {
                body0 = RequestBody.create(MediaType.parse(this.contentType), this.body);
            }
            return body0;
        }

        protected Headers getHeaders() {
            return null;
        }
    }
}
