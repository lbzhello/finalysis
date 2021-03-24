package xyz.liujin.finalysis.analysis.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.base.dto.KLineDto;
import xyz.liujin.finalysis.base.service.KLineService;

import java.time.LocalDate;
import java.util.List;

/**
 * 日线数据
 */
@Api(tags = "日线数据")
@RestController
@RequestMapping("/k")
public class KLineController {
    @Autowired
    private KLineService kLineService;

    @GetMapping("/calculate/volume-ratio")
    @ApiOperation("计算量比信息")
    public Flux<String> calculateVolumeRatio(
            @ApiParam("开始日期")
            @RequestParam(name = "start", required = false) LocalDate start,

            @ApiParam("结束日期")
            @RequestParam(name = "end", required = false) LocalDate end,

            @ApiParam("股票列表")
            @RequestParam(name = "codes", required = false) List<String> codes
            ) {
        kLineService.calculateVolumeRatio(start, end, codes);
        return Flux.just("start to calculate volume ratio...");
    }

    @ApiOperation(value = "过去股票当日 k 线")
    @GetMapping("{code}/day")
    public Flux<KLineDto> queryDay(
            @ApiParam(value = "股票代码")
            @PathVariable(name = "code") String code
    ) {
        return kLineService.findByCodeAndOrderByDateDesc(code, null, null);
    }
}
