package xyz.liujin.finalysis.analysis.dto;

import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

@Tag(name = "量比放量指标")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class HeavenVolRatioQo {
    @Schema(description = "统计日期，默认数据库最新", hidden = true)
    private LocalDate date;
    @Schema(description = "统计天数，已当前天数为基准", example = "10")
    private Integer days;
    @Schema(description = "days 天内存在最小量比", example = "1.5")
    private BigDecimal minVolRatio;
    @Schema(description = "需要统计的股票列表", hidden = true)
    private List<String> codes;

    // 默认查询类, 10 天内存在量比大于等于 1.6
    public static HeavenVolRatioQo DEFAULT = HeavenVolRatioQo.builder()
            .days(6)
            .minVolRatio(BigDecimal.valueOf(2))
            .build();
}

