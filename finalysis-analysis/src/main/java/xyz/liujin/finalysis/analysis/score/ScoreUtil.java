package xyz.liujin.finalysis.analysis.score;

import cn.hutool.core.collection.CollectionUtil;
import cn.hutool.core.text.CharSequenceUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.lang.Nullable;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.score.annation.ScoreField;
import xyz.liujin.finalysis.analysis.score.annation.ScorePage;
import xyz.liujin.finalysis.analysis.score.annation.ScoreType;
import xyz.liujin.finalysis.analysis.service.ScoreService;
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
@Configuration
public class ScoreUtil {
    private static final MyLogger logger = MyLogger.getLogger(ScoreUtil.class);

    private static ScoreService scoreService;
    @Autowired
    public void setScoreService(ScoreService scoreService) {
        ScoreUtil.scoreService = scoreService;
    }

    /**
     * 获取分数
     *
     * 目标类 obj 必须实现 {@link Scoreable} 接口或者加上 {@link xyz.liujin.finalysis.analysis.score.annation.ScoreType} 注解
     *
     * @param obj 根据 obj 计算分数
     * @return 分数
     *
     * @see xyz.liujin.finalysis.analysis.score.annation.ScoreType
     * @see xyz.liujin.finalysis.analysis.score.annation.ScoreField
     * @see Scoreable
     */
    public static Score getScore(@Nullable Object obj) {
        Score score = calculateScore(obj);
        if (Objects.nonNull(score)) {
            scoreService.refreshScore(score);
        }

        return score;
    }

    /**
     * 计算分数
     *
     * 目标类 obj 必须实现 {@link Scoreable} 接口或者加上 {@link xyz.liujin.finalysis.analysis.score.annation.ScoreType} 注解
     *
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
        ScoreType st = objClass.getAnnotation(ScoreType.class);
        if (Objects.isNull(st)) {
            return null;
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


        ScoreTypeEnum scoreTypeEnum  = st.value();
        String scoreCode = "";
        if (CollectionUtil.isNotEmpty(scoreCodes)) {
            scoreCode = scoreTypeEnum.getType() + st.codePrefix() +
                    CollectionUtil.join(scoreCodes, st.codeSeparator()) +
                    st.codeSuffix();
        }

        String description = "";
        if (CollectionUtil.isNotEmpty(scoreDescs)) {
            description = st.descriptionPrefix() +
                    CollectionUtil.join(scoreDescs, st.descriptionSeparator()) +
                    st.descriptionSuffix();
        }
        return Score.builder()
                .scoreCode(scoreCode)
                .score(st.score())
                .type(scoreTypeEnum.getType())
                .description(description)
                .build();
    }
}