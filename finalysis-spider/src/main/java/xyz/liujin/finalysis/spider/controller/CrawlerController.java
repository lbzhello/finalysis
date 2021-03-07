package xyz.liujin.finalysis.spider.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.spider.manager.CrawlManager;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping("crawl")
public class CrawlerController {
    @Autowired
    private CrawlManager crawlManager;

    @ApiOperation("更新股票数据")
    @GetMapping("stock")
    public Flux<String> refreshStock() {
        return crawlManager.refreshStock();
    }


    @ApiOperation("更新 K 线数据")
    @GetMapping("k")
    public Flux<String> refreshKLine(
            @ApiParam(value = "开始日期 yyyy-MM-dd；默认当日", example = "2021-02-12")
            @RequestParam(name = "start", required = false) LocalDate start,

            @ApiParam(value = "结束日期 yyyy-MM-dd；默认当日", example = "2021-02-12")
            @RequestParam(name = "end", required = false) LocalDate end,

            @ApiParam(value = "股票列表，默认所有股票", example = "000001,600001")
            @RequestParam(name = "codes", required = false) List<String> codes) {
        return crawlManager.refreshKLine(start, end, codes);
    }
}