package xyz.liujin.finalysis.analysis.score.annation;

import java.lang.annotation.*;

/**
 * 用于根据 {@link xyz.liujin.finalysis.base.page.PageQo} 生成分数描述信息
 * 由于注解的侵入性较强，这里单独配置一个类
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/25
 * @see xyz.liujin.finalysis.analysis.entity.Score
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScorePage {
    String ORDER_BY = "orderBy";
    String LIMIT = "limit";
}
