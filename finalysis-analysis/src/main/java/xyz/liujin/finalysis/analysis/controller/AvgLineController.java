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

    @ApiOperation("获取 5 日线突破十日线的股票")
    @GetMapping("5-cross-10")
    public Mono<List<String>> fiveCrossTen(
            @ApiParam(value = "最大突破天数", example = "3")
            @RequestParam(name = "days", required = false) Integer days
    ) {
        return avgLineService.fiveCrossTen(Optional.ofNullable(days).orElse(3)).collectList();
    }

    @ApiOperation("获取 5 日线在 10 日线上方的股票的股票")
    @GetMapping("5-above-10")
    public Mono<List<String>> fiveAboveTen(
            @ApiParam(value = "最小持续天数", example = "3")
            @RequestParam(name = "days", required = false) Integer days
    ) {
        return avgLineService.fiveAboveTen(Optional.ofNullable(days).orElse(3)).collectList();
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
