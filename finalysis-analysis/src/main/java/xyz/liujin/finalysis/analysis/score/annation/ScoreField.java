package xyz.liujin.finalysis.analysis.score.annation;

import java.lang.annotation.*;

/**
 * 得分字段注解
 * 用来生成分数代码和说明信息
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/25
 * @see xyz.liujin.finalysis.analysis.entity.Score
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScoreField {
    /**
     * 等效 {@link #description()}
     * @return
     */
    String value() default "";

//    /**
//     * 重写字段名称，默认为字段名，用于生成得分码
//     * 支持 %s 占位符，字段值将注入占位符
//     * @return
//     */
//    String name() default "";

    /**
     * 字段说明，用来生成字段说明信息
     * 支持 %s 占位符，字段值将注入占位符
     * @return
     */
    String description() default "";
}
