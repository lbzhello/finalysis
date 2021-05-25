package xyz.liujin.finalysis.analysis.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.DailyDataQo;
import xyz.liujin.finalysis.analysis.dto.RecommendQo;
import xyz.liujin.finalysis.analysis.service.AnalysisService;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.dto.DailyData;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Optional;

@Api("股票分析实体类")
@RestController
@RequestMapping("analysis")
public class AnalysisController {
    @Autowired
    private AnalysisService analysisService;

    @ApiOperation("推荐股票")
    @PostMapping("recommend")
    public Flux<DailyData> recommend(@RequestBody RecommendQo req) {
        return analysisService.recommend(req);
    }

    @ApiOperation("获取股票日数据")
    @PostMapping("daily")
    public Flux<DailyData> dailyData(@RequestBody DailyDataQo req) {
        return analysisService.dailyData(req);
    }

    @ApiOperation("获取最近放量的股票")
    @GetMapping("heaven-volume-ratio")
    public Flux<DailyData> heavenVolumeRatio(
            @ApiParam("统计天数；默认 5")
            @RequestParam(name = "days", defaultValue = "5") Integer days,
            @ApiParam("统计天数内存在的最小量比")
            @RequestParam(name = "minVolRatio", defaultValue = "2") BigDecimal minVolRatio) {
        days = ObjectUtils.firstNonNull(days, 5);
        minVolRatio = ObjectUtils.firstNonNull(minVolRatio, BigDecimal.valueOf(2));
        return analysisService.heavenVolumeRatioDetail(days, minVolRatio);
    }

    @ApiOperation("获取 5 日线突破十日线的股票")
    @GetMapping("5-cross-10")
    public Flux<DailyData> fiveCrossTen(
            @ApiParam(value = "最大突破天数", example = "3")
            @RequestParam(name = "days", required = false) Integer days,
            @ApiParam(value = "当前日期，默认数据库最新", example = "3")
            @RequestParam(name = "date", required = false) LocalDate date
    ) {
        return analysisService.fiveCrossTenDetail(Optional.ofNullable(days).orElse(3), date);
    }

    @ApiOperation("获取 5 日线在 10 日线上方的股票的股票")
    @GetMapping("5-above-10")
    public Flux<DailyData> fiveAboveTen(
            @ApiParam(value = "最小持续天数", example = "3")
            @RequestParam(name = "days", required = false) Integer days,
            @ApiParam(value = "当前日期，默认数据库最新")
            @RequestParam(name = "date", required = false) LocalDate date
    ) {
        return analysisService.fiveAboveTenDetail(Optional.ofNullable(days).orElse(3), date);
    }
}
