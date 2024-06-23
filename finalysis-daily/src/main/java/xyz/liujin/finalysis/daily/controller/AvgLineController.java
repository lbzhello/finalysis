package xyz.liujin.finalysis.daily.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.daily.service.AvgLineService;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("avg")
public class AvgLineController {
    @Autowired
    private AvgLineService avgLineService;

    @Operation(summary = "更新均线并入库")
    @GetMapping("/refresh")
    public Flux<String> refresh(
            @Parameter(description = "开始日期 yyyy-MM-dd；默认当天", example = "2021-02-12")
            @RequestParam(name = "start", required = false) LocalDate start,

            @Parameter(description = "结束日期 yyyy-MM-dd；默认当天", example = "2021-02-12")
            @RequestParam(name = "end", required = false) LocalDate end,

            @Parameter(description = "股票代码；默认所有股票", example = "000001,000002")
            @RequestParam(name = "codes", required = false) String codes) {

        List<String> stockCodes = Optional.ofNullable(codes).map(it -> Arrays.asList(it.split(","))).orElse(List.of());

        avgLineService.refreshAvgLine(start, end, stockCodes);
        return Flux.just("start refresh avg");
    }
}
