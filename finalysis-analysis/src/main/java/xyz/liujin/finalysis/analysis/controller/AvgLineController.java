package xyz.liujin.finalysis.analysis.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.DayAvgLine;
import xyz.liujin.finalysis.analysis.qo.AvgLineQo;
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

    @ApiOperation("获取上升趋势的股票")
    @GetMapping("upwards")
    public Flux<DayAvgLine> upwards(
            @ApiParam(value = "开始日期；默认最后一个交易日", example = "2021-02-20")
            @RequestParam(name = "start", required = false) LocalDate start,

            @ApiParam(value = "结束日期；默认最后一个交易日", example = "2021-02-20")
            @RequestParam(name = "end", required = false) LocalDate end,

            @ApiParam(value = "股票代码；默认全部", example = "000001,000002")
            @RequestParam(name = "codes", required = false) List<String> codes,

            @ApiParam(value = "数量限制；默认 1000", example = "1000")
            @RequestParam(name = "limit", required = false) Integer limit
    ) {
        AvgLineQo qo = AvgLineQo.builder()
                .start(start)
                .end(end)
                .stockCodes(codes)
                .limit(Optional.ofNullable(limit).orElse(1000))
                .build();
        return avgLineService.upwards(qo);
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
        LocalDate startDate = Optional.ofNullable(start).orElse(LocalDate.now());
        LocalDate endDate = Optional.ofNullable(end).orElse(LocalDate.now());
        List<String> stockCodes = Optional.ofNullable(codes).map(it -> Arrays.asList(it.split(","))).orElse(List.of());

        avgLineService.refreshAvgLine(startDate, endDate, stockCodes);
        return Flux.just("start refresh avg");
    }
}
