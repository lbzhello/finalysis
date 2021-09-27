package com.liujin.finalysis.analysis.dto;

import org.junit.jupiter.api.Test;
import xyz.liujin.finalysis.analysis.dto.IncreaseRatioQo;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.base.page.PageQo;

import java.math.BigDecimal;

/**
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/26
 */
public class IncreaseRatioQoTest {
    @Test
    public void getScoreTest() {
        IncreaseRatioQo increaseRatioQo = IncreaseRatioQo.builder()
                .recDays(3)
                .hisDays(5)
                .minRatio(BigDecimal.valueOf(2))
                .page(PageQo.builder()
                        .limit(100)
                        .orderBy("pct_change_ratio desc")
                        .build())
                .build();
        Score score = increaseRatioQo.getScore();
        System.out.println(score);
    }
}
