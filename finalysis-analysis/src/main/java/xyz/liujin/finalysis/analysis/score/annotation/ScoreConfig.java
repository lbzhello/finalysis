package xyz.liujin.finalysis.analysis.score.annotation;

import xyz.liujin.finalysis.analysis.score.ScoreType;

import java.lang.annotation.*;

/**
 * 得分类型注解
 * 用在类上，用于生成分数代码(score_code)，分数类型(type),分数描述(description)等信息
 * 分数码格式：value() + codePrefix + [字段键值] + codeSeparator + [字段键值] + codeSuffix
 * 分数码说明格式：descriptionPrefix() + descriptionPrefix() + descriptionSuffix()
 * e.g.
 * stock_code = increase_ratio(recDays=3, hisDays=5)
 * description = 最近 3 天,过去 5 天,增幅比大于 2 的股票
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/25
 * @see ScoreField
 * @see xyz.liujin.finalysis.analysis.entity.Score
 */
@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
@Documented
public @interface ScoreConfig {
    /**
     * 分数类型
     * @return
     */
    ScoreType value();

    /**
     * 得分
     */
    int score() default 10;

    /**
     * 分数码分隔符
     */
    String codeSeparator() default ",";

    /**
     * 分数码前缀
     */
    String codePrefix() default "(";

    /**
     * 分数码后缀
     */
    String codeSuffix() default ")";

    /**
     * 说明信息分隔符
     */
    String descriptionSeparator() default ",";

    /**
     * 说明信息拼接前缀
     */
    String descriptionPrefix() default "";

    /**
     * 说明信息拼接后缀
     */
    String descriptionSuffix() default ";";
}
