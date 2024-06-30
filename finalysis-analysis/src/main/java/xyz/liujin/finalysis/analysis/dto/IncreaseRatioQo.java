package xyz.liujin.finalysis.analysis.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import xyz.liujin.finalysis.analysis.entity.Score;
import xyz.liujin.finalysis.analysis.score.ScoreType;
import xyz.liujin.finalysis.analysis.score.Scoreable;
import xyz.liujin.finalysis.analysis.score.annotation.ScoreConfig;
import xyz.liujin.finalysis.analysis.strategy.StrategyQo;
import xyz.liujin.finalysis.base.page.PageQo;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

/**
 * 增幅指标
 * 最近 recDays 天增幅，与过去 hisDays 天增幅的比值
 * 最近几天股票增幅巨大，过去几天涨幅较小，龙抬头
 * 说明股票可能有重大利好消息
 */
@Tag(name = "增幅指标")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ScoreConfig(value = ScoreType.INCREASE_RATIO)
public class IncreaseRatioQo implements Scoreable, StrategyQo {
    @Schema
    private LocalDate date;

    @Schema(description = "需要统计的最近的天数", example = "3")
    private Integer recDays;

    @Schema(description = "需要统计的过去的天数", example = "5")
    private Integer hisDays;

    @Schema(description = "过去天数与最近天数增幅比值最小值", example = "2")
    private BigDecimal minRatio;

    @Schema(description = "最近最小增幅", example = "10")
    private BigDecimal minRecInc;

    @Schema(description = "最近最大增幅", example = "50")
    private BigDecimal maxRecInc;

    @Schema(description = "需要统计的股票列表", hidden = true)
    private List<String> codes;

    @Schema(description = "分页信息")
    private PageQo page;

    @Override
    public String getType() {
        return ScoreType.INCREASE_RATIO;
    }

    private static final String TAG_PREFIX = ScoreType.INCREASE_RATIO + "(";
    @Override
    public Score getScore() {
        // 格式：increase_ratio(recDays=3, hisDays=5)
        StringBuilder scoreCode = new StringBuilder(TAG_PREFIX);

        // 格式：最近 3 天，过去 5 天
        StringBuilder desc = new StringBuilder("增幅比指标,");

        if (Objects.nonNull(recDays)) {
            scoreCode.append("recDays=").append(recDays).append(",");
            desc.append("最近").append(recDays).append("天,");
        }

        if (Objects.nonNull(hisDays)) {
            scoreCode.append("hisDays=").append(hisDays).append(",");
            desc.append("过去").append(hisDays).append("天,");
        }

        if (Objects.nonNull(minRatio)) {
            scoreCode.append("minRatio=").append(minRatio).append(",");
            desc.append("增幅比大于").append(minRatio).append(",");
        }

        if (Objects.nonNull(page)) {
            if (Objects.nonNull(page.getOrderBy())) {
                scoreCode.append("orderBy=").append(page.getOrderBy()).append(",");
                desc.append("根据").append(page.getOrderBy()).append("排序,");
            }

            if (Objects.nonNull(page.getLimit())) {
                scoreCode.append("limit=").append(page.getLimit()).append(",");
                desc.append("前").append(page.getLimit()).append("条数据,");
            }
        }

        String scoreCodeStr = scoreCode.toString();
        // 去掉结尾 ,
        if (scoreCodeStr.endsWith(",")) {
            scoreCodeStr = scoreCodeStr.substring(0, scoreCode.length() - 1);
        }
        scoreCodeStr += ")";

        String descStr = desc.toString();
        // 去掉结尾 ,
        if (descStr.endsWith(",")) {
            descStr = descStr.substring(0, desc.length() - 1);
        }
        descStr += ";";

        return Score.builder()
                .type(ScoreType.INCREASE_RATIO)
                .scoreCode(scoreCodeStr)
                .score(10)
                .description(descStr)
                .build();
    }

}
