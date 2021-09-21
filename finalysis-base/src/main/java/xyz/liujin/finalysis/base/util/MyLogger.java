package xyz.liujin.finalysis.base.util;

import cn.hutool.core.util.ArrayUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;
import reactor.util.function.Tuples;

import java.util.ArrayList;
import java.util.List;

/**
 * 日志打印规范化
 */
public final class MyLogger {
    private Logger logger;

    /**
     * @see #getLogger(Class)
     * @param logger
     */
    private MyLogger(Logger logger) {
        this.logger = logger;
    }

    /**
     * 获取 Logger
     * @param clazz
     * @return
     */
    public static MyLogger getLogger(Class<?> clazz) {
        Logger logger = LoggerFactory.getLogger(clazz);
        return new MyLogger(logger);
    }

    public void trace(String msg, Object... params) {
        format(msg, params).subscribe(it -> logger.trace(it.getT1(), it.getT2()));
    }

    public void debug(String msg, Object... params) {
        format(msg, params).subscribe(it -> logger.debug(it.getT1(), it.getT2()));
    }

    public void info(String msg, Object... params) {
        format(msg, params).subscribe(it -> logger.info(it.getT1(), it.getT2()));
    }

    public void warn(String msg, Object... params) {
        format(msg, params).subscribe(it -> logger.warn(it.getT1(), it.getT2()));
    }

    public void warn(String msg, Throwable throwable) {
        logger.warn(msg, throwable);
    }

    public void error(String msg, Object... params) {
        format(msg, params).subscribe(it -> logger.error(it.getT1(), it.getT2()));
    }

    public void error(String msg, Throwable throwable) {
        logger.error(msg, throwable);
    }

    /**
     * params 长度必须为偶数
     * 会被格式化为 [name=value] 形式
     * e.g.
     * ("n1", "lili", "n2", "tom") => [n1=lili] [n2=tom]
     */
    private Mono<Tuple2<String, Object[]>> format(String msg, Object... params) {
        if (ArrayUtil.isEmpty(params)) {
            return Mono.just(Tuples.of(msg, params));
        }

        // param 长度必须是偶数
        if (params.length % 2 != 0) {
            throw new IllegalArgumentException("params length mast be even number");
        }

        List<Object> paramNames = new ArrayList<>();
        for (int i = 0; i < params.length; i += 2) {
            paramNames.add(params[i]);
        }

        List<Object> paramValues = new ArrayList<>();
        for (int i = 1; i < params.length; i += 2) {
            paramValues.add(params[i]);
        }

        return Mono.just(Tuples.of(fmtParamNames(msg, paramNames), paramValues.toArray()));
    }

    // 格式化参数名 p1, p2, p3 -> [p1={}] [p2={}] [p3={}]
    private String fmtParamNames(String msg, List<Object> paramNames) {
        return paramNames.stream()
                .map(name -> "[" + name + "={}" + "]")
                .reduce((a, b) -> a + " " + b)
                .map(it -> it + " " + msg)
                .orElse("");
    }
}
