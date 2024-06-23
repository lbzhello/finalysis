package xyz.liujin.finalysis.daily.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.daily.dto.KLineDto;
import xyz.liujin.finalysis.daily.service.KLineService;

import java.time.LocalDate;
import java.util.List;

/**
 * 日线数据
 */
@Tag(name = "日线数据")
@RestController
@RequestMapping("/k")
public class KLineController {
    @Autowired
    private KLineService kLineService;

    @GetMapping("/calculate/volume-ratio")
    @Operation(summary = "计算量比信息")
    public Flux<String> calculateVolumeRatio(
            @Parameter(description = "开始日期")
            @RequestParam(name = "start", required = false) LocalDate start,

            @Parameter(description = "结束日期")
            @RequestParam(name = "end", required = false) LocalDate end,

            @Parameter(description = "股票列表")
            @RequestParam(name = "codes", required = false) List<String> codes
            ) {
        kLineService.calculateVolumeRatio(start, end, codes);
        return Flux.just("start to calculate volume ratio...");
    }

    @Operation(summary = "过去股票当日 k 线")
    @GetMapping("{code}/day")
    public Flux<KLineDto> queryDay(
            @Parameter(description = "股票代码")
            @PathVariable(name = "code") String code
    ) {
        return kLineService.findByCodeAndOrderByDateDesc(code, null, null);
    }
}
