package xyz.liujin.finalysis.analysis.score;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.lang.Nullable;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.score.annotation.ScoreConfig;
import xyz.liujin.finalysis.analysis.score.annotation.ScoreField;
import xyz.liujin.finalysis.analysis.score.annotation.ScorePage;
import xyz.liujin.finalysis.base.util.MyLogger;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

/**
 * 股票得分工具类
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/25
 * @see Scoreable
 */
public class ScoreUtil {
    private static final MyLogger logger = MyLogger.getLogger(ScoreUtil.class);

    /**
     * 计算分数
     *
     * 目标类 obj 需要加上 {@link ScoreConfig} 注解
     *
     * @see ScoreConfig
     * @see xyz.liujin.finalysis.analysis.score.annotation.ScoreField
     * @param obj
     * @return
     */
    public static Score calculateScore(@Nullable Object obj) {
        if (Objects.isNull(obj)) {
            return null;
        }

        // 如果实现了 Scoreable 接口，则通过接口生成 Score
        if (obj instanceof Scoreable) {
            Score score = ((Scoreable) obj).getScore();
            if (Objects.nonNull(score)) {
                return score;
            }
        }

        List<String> scoreCodes = new ArrayList<>();
        List<String> scoreDescs = new ArrayList<>();

        // 根据注解解析
        Class<?> objClass = obj.getClass();
        ScoreConfig st = objClass.getAnnotation(ScoreConfig.class);
        if (Objects.isNull(st)) {
            return null;
        }

        // 得分
        int score = st.score();

        // 得分自定义配置接口
        if (obj instanceof ScoreCustomer) {
            int s = ((ScoreCustomer) obj).getScore();
            if (s != 0) {
                score = s;
            }
        }

        Field[] fields = objClass.getDeclaredFields();
        Arrays.stream(fields)
                // Score
                .forEach(field -> {
                    try {
                        ScoreField sf = field.getAnnotation(ScoreField.class);
                        if (Objects.nonNull(sf)) {
                            // 分数代码
                            String fieldName = field.getName();
                            field.setAccessible(true);
                            Object fieldValue = field.get(obj);
                            if (Objects.isNull(fieldValue)) {
                                return;
                            }

                            // 分数代码
                            String scoreCodeFmt = fieldName + "=%s";
                            String scoreCode = String.format(scoreCodeFmt, fieldValue);
                            scoreCodes.add(scoreCode);

                            // 得分描述信息
                            String descFmt = sf.value();
                            if (CharSequenceUtil.isEmpty(descFmt)) {
                                descFmt = sf.description();
                            }
                            if (CharSequenceUtil.isNotEmpty(descFmt)) {
                                String desc = String.format(descFmt, fieldValue);
                                scoreDescs.add(desc);
                            }

                        }

                        // 分页信息
                        if (Objects.nonNull(field.getAnnotation(ScorePage.class))) {
                            field.setAccessible(true);
                            Object pageObj = field.get(obj);
                            if (Objects.isNull(pageObj)) {
                                return;
                            }

                            Class<?> pageClass = pageObj.getClass();

                            Field orderByField = pageClass.getDeclaredField(ScorePage.ORDER_BY);
                            orderByField.setAccessible(true);
                            Object orderBy = orderByField.get(pageObj);
                            scoreCodes.add("%s=%s".formatted(ScorePage.ORDER_BY, orderBy));
                            scoreDescs.add("根据 %s 排序".formatted(orderBy));

                            Field limitField = pageClass.getDeclaredField(ScorePage.LIMIT);
                            limitField.setAccessible(true);
                            Object limit = limitField.get(pageObj);
                            scoreCodes.add("%s=%s".formatted(ScorePage.LIMIT, limit));
                            scoreDescs.add("前 %s 条数据".formatted(limit));
                        }
                    } catch (IllegalAccessException | NoSuchFieldException e) {
                        logger.error("failed to get value", e);
                    }
                });


        ScoreType scoreType = st.value();
        String scoreCode = scoreType.getType() + st.codePrefix() +
                CollectionUtil.join(scoreCodes, st.codeSeparator()) +
                st.codeSuffix();

        String description = st.descriptionPrefix() +
                CollectionUtil.join(scoreDescs, st.descriptionSeparator()) +
                st.descriptionSuffix();

        return Score.builder()
                .scoreCode(scoreCode)
                .score(score)
                .type(scoreType.getType())
                .description(description)
                .build();
    }
}
