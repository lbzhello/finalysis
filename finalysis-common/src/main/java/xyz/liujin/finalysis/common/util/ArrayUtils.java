package xyz.liujin.finalysis.common.util;

import java.lang.reflect.Array;

public class ArrayUtils {
    /**
     * 对象是否为数组对象
     *
     * @param obj 对象
     * @return 是否为数组对象，如果为{@code null} 返回false
     */
    public static boolean isArray(Object obj) {
        if (null == obj) {
            return false;
        }
        return obj.getClass().isArray();
    }

    /**
     * 将 obj (原本类型为数组)转成数组
     * @param objArr
     * @param <T>
     * @return
     */
    public static <T> T[] asArray(Object objArr, Class<? extends T[]> newType) {
        int len = Array.getLength(objArr);
        @SuppressWarnings("unchecked")
        T[] newArr = ((Object)newType == (Object)Object[].class)
                ? (T[]) new Object[len]
                : (T[]) Array.newInstance(newType.getComponentType(), len);
        System.arraycopy(objArr, 0, newArr, 0, len);
        return newArr;
    }
}
