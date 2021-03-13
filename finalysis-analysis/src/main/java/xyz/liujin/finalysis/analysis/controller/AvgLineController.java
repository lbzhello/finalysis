package xyz.liujin.finalysis.analysis.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.analysis.dto.DayAvgLine;
import xyz.liujin.finalysis.analysis.service.AvgLineService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("avg")
public class AvgLineController {
    @Autowired
    private AvgLineService avgLineService;

    @ApiOperation("统计启动（均线突破)阶段的股票，5 日线刚超过十日线")
    @GetMapping("start-up")
    public Flux<DayAvgLine> startUp(
            @ApiParam(value = "最大启动天数（5 日线超过 10 日线）", example = "3")
            @RequestParam(name = "days", required = false) Integer days
    ) {
        return Flux.just();
    }

    @ApiOperation("获取上升趋势的股票")
    @GetMapping("upwards")
    public Mono<List<String>> upwards(
            @ApiParam(value = "最小持续天数", example = "3")
            @RequestParam(name = "days", required = false) Integer days
    ) {
        return avgLineService.upwards(days).collectList();
    }

    @ApiOperation("更新均线并入库")
    @GetMapping("/refresh")
    public Flux<String> refresh(
            @ApiParam(value = "开始日期 yyyy-MM-dd；默认当天", example = "2021-02-12")
            @RequestParam(name = "start", required = false) LocalDate start,

            @ApiParam(value = "结束日期 yyyy-MM-dd；默认当天", example = "2021-02-12")
            @RequestParam(name = "end", required = false) LocalDate end,

            @ApiParam(value = "股票代码；默认所有股票", example = "000001,000002")
            @RequestParam(name = "codes", required = false) String codes) {

        List<String> stockCodes = Optional.ofNullable(codes).map(it -> Arrays.asList(it.split(","))).orElse(List.of());

        avgLineService.refreshAvgLine(start, end, stockCodes);
        return Flux.just("start refresh avg");
    }
}
