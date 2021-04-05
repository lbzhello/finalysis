package xyz.liujin.finalysis.base.util;

import java.util.Objects;

public class ObjectUtils {
    /**
     * 返回第一个非 null 的对象
     * @param objs
     * @param <T>
     * @return
     */
    public static <T> T firstNonNull(T... objs) {
        for (T obj : objs) {
            if (Objects.nonNull(obj)) {
                return obj;
            }
        }

        throw new IllegalArgumentException("null");
    }

}
