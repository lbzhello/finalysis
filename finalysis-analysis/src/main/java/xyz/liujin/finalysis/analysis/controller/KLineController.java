package xyz.liujin.finalysis.analysis.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.common.dto.KLineDto;
import xyz.liujin.finalysis.common.service.KLineService;

/**
 * 日线数据
 */
@Api(tags = "日线数据")
@RestController
@RequestMapping("/k")
public class KLineController {
    @Autowired
    private KLineService kLineService;

    @ApiOperation(value = "获取股票日线数据")
    @GetMapping
    public Flux<KLineDto> query(@ApiParam(name = "code", value = "股票代码") @RequestParam(name = "code", required = false) String code) {
        return kLineService.getByCode(code, null, null);
    }

    @ApiOperation(value = "过去股票当日 k 线")
    @GetMapping("{code}/day")
    public Flux<KLineDto> queryDay(@PathVariable(name = "code") String code) {
        return query(code);
    }
}
