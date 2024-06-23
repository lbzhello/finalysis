package xyz.liujin.finalysis.analysis.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.util.List;

@Schema(description = "日线突破指标")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class FiveCrossTenQo {
    @Schema(description = "最大突破天数，5 日线超过 10 日线的最大天数", example = "3")
    private Integer days;

    @Schema(description = "当前日期，默认数据库最新", hidden = true)
    private LocalDate date;

    @Schema(description = "需要统计的股票列表", hidden = true)
    private List<String> codes;

    public static FiveCrossTenQo DEFAULT = FiveCrossTenQo.builder()
            .days(3)
            .build();
}
