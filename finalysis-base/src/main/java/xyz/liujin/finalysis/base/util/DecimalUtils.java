package xyz.liujin.finalysis.base.util;

import org.springframework.lang.Nullable;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * 数值计算通用工具类
 */
public class DecimalUtils {
    /**
     * 判断数值是否为 0(0.00, 0.000, 0.000...) 值
     * @param bigDecimal
     * @return
     */
    public static final boolean isZero(@Nullable BigDecimal bigDecimal) {
        return Objects.nonNull(bigDecimal) && BigDecimal.ZERO.compareTo(bigDecimal) == 0;
    }

    public static final boolean isNotZero(@Nullable BigDecimal bigDecimal) {
        return !isZero(bigDecimal);
    }

    /**
     * 判断是否为 0(0.00, 0.000, 0.000...)或 null
     * @return
     */
    public static final boolean isZeroOrNull(@Nullable BigDecimal bigDecimal) {
        return Objects.isNull(bigDecimal) || isZero(bigDecimal);
    }

    public static final boolean isNotZeroNull(@Nullable BigDecimal bigDecimal) {
        return !isZeroOrNull(bigDecimal);
    }
}
