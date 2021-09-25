package com.liujin.finalysis.analysis.score;

import org.junit.jupiter.api.Test;
import xyz.liujin.finalysis.analysis.dto.TurnRatioQo;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.score.ScoreUtil;
import xyz.liujin.finalysis.base.page.PageQo;

import java.math.BigDecimal;

/**
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/26
 */
public class ScoreUtilTest {
    @Test
    public void scoreUtilTest() {
        Score score = ScoreUtil.getScore(TurnRatioQo.builder()
                .recDays(3)
                .hisDays(5)
                .minAvgAmount(BigDecimal.valueOf(1e9))
                .page(PageQo.builder()
                        .orderBy("rec_avg_turn_ratio desc")
                        .limit(100)
                        .build())
                .build());
        System.out.println();
    }
}
