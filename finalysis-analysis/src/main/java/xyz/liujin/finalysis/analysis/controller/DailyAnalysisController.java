package xyz.liujin.finalysis.analysis.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.analysis.dto.DailyDateQo;
import xyz.liujin.finalysis.analysis.service.DailyAnalysisService;
import xyz.liujin.finalysis.daily.dto.DailyData;
import xyz.liujin.finalysis.daily.service.AvgLineService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Api("股票分析实体类")
@RestController
@RequestMapping("analysis")
public class DailyAnalysisController {
    @Autowired
    private DailyAnalysisService dailyAnalysisService;

    @ApiOperation("获取股票日数据")
    @PostMapping("daily")
    public Flux<DailyData> dailyData(@RequestBody DailyDateQo req) {
        return dailyAnalysisService.dailyData(req);
    }

    @Autowired
    private AvgLineService avgLineService;

    @ApiOperation("获取 5 日线突破十日线的股票")
    @GetMapping("5-cross-10")
    public Mono<List<String>> fiveCrossTen(
            @ApiParam(value = "最大突破天数", example = "3")
            @RequestParam(name = "days", required = false) Integer days,
            @ApiParam(value = "当前日期，默认数据库最新", example = "3")
            @RequestParam(name = "date", required = false) LocalDate date
    ) {
        return avgLineService.fiveCrossTen(Optional.ofNullable(days).orElse(3), date).collectList();
    }

    @ApiOperation("获取 5 日线在 10 日线上方的股票的股票")
    @GetMapping("5-above-10")
    public Mono<List<DailyData>> fiveAboveTen(
            @ApiParam(value = "最小持续天数", example = "3")
            @RequestParam(name = "days", required = false) Integer days,
            @ApiParam(value = "当前日期，默认数据库最新")
            @RequestParam(name = "date", required = false) LocalDate date
    ) {
        return dailyAnalysisService.fiveAboveTenDetail(Optional.ofNullable(days).orElse(3),
                date).collectList();
    }
}
