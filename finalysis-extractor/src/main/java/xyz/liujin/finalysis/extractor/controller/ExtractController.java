package xyz.liujin.finalysis.extractor.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.extractor.manager.ExtractManager;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("extract")
public class ExtractController {
    @Autowired
    private ExtractManager extractManager;

    @ApiOperation(value = "启动所有任务", notes = "1. 更新股票数据；2. 更新 k 线数据；")
    @GetMapping("all")
    public Flux<String> all() {
        return Flux.merge(extractStock(), extractKLine(null, null, null));
    }

    @ApiOperation("更新股票数据")
    @GetMapping("stock")
    public Flux<String> extractStock() {
        return extractManager.refreshStock();
    }


    @ApiOperation("更新 K 线数据")
    @GetMapping("k")
    public Flux<String> extractKLine(
            @ApiParam(value = "开始日期 yyyy-MM-dd；默认数据库最新数据", example = "2021-02-12")
            @RequestParam(name = "start", required = false) LocalDate start,

            @ApiParam(value = "结束日期 yyyy-MM-dd；默认当日", example = "2021-02-12")
            @RequestParam(name = "end", required = false) LocalDate end,

            @ApiParam(value = "股票列表，默认所有股票", example = "000001,600001")
            @RequestParam(name = "codes", required = false) List<String> codes) {
        return extractManager.refreshKLine(start, end, codes);
    }
}
