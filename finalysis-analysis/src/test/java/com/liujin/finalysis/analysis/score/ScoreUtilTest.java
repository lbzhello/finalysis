package com.liujin.finalysis.analysis.score;

import org.junit.jupiter.api.Test;
import xyz.liujin.finalysis.analysis.dto.TurnRatioQo;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.score.ScoreUtil;
import xyz.liujin.finalysis.base.page.PageQo;

import java.lang.reflect.Field;
import java.math.BigDecimal;

/**
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/26
 */
public class ScoreUtilTest {
    @Test
    public void scoreUtilTest() {
        Score score = ScoreUtil.calculateScore(TurnRatioQo.builder()
                .recDays(3)
                .hisDays(5)
                .minAvgAmount(BigDecimal.valueOf(1e9))
                .page(PageQo.builder()
                        .orderBy("turn_ratio desc")
                        .limit(100)
                        .build())
                .build());
        System.out.println();
    }

    /**
     * 调用 {@link Field#setAccessible(boolean)}} 方法后，才可以访问私有字段
     * 同时不会影响字段的访问修饰符
     * @throws NoSuchFieldException
     */
    @Test
    public void reflectTest() throws NoSuchFieldException {
        TurnRatioQo obj = TurnRatioQo.builder()
                .recDays(3)
                .build();
        Class<? extends TurnRatioQo> aClass = obj.getClass();
        Field field = aClass.getDeclaredField("recDays");
        int modifiers = field.getModifiers();
        boolean canAccess = field.canAccess(obj);
        boolean canAccessAgain = obj.getClass().getDeclaredField("recDays").canAccess(obj);
        System.out.println();
    }
}
