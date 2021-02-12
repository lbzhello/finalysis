package xyz.liujin.finalysis.analysis.controller;

import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.qo.AvgLineQo;
import xyz.liujin.finalysis.analysis.service.AvgLineService;
import xyz.liujin.finalysis.common.util.DateUtils;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping("avg")
public class AvgLineController {
    @Autowired
    private AvgLineService avgLineService;

    @ApiOperation("更新均线并入库")
    @GetMapping("/refresh")
    public Flux<String> refresh(
            @ApiParam(value = "开始日期 yyyy-MM-dd；默认当天", example = "2021-02-12")
            @RequestParam(name = "start", required = false) String start,

            @ApiParam(value = "结束日期 yyyy-MM-dd；默认当天", example = "2021-02-12")
            @RequestParam(name = "end", required = false) String end,

            @ApiParam(value = "股票代码；默认所有股票", example = "000001,000002")
            @RequestParam(name = "codes", required = false) String codes) {
        AvgLineQo qo = AvgLineQo.builder()
                .startDate(Optional.ofNullable(start).map(DateUtils::parseDate).orElse(LocalDate.now()))
                .endDate(Optional.ofNullable(end).map(DateUtils::parseDate).orElse(LocalDate.now()))
                .stockCodes(Optional.ofNullable(codes).map(it -> Arrays.asList(it.split(","))).orElse(List.of()))
                .build();
        avgLineService.refreshAvgLine(qo);
        return Flux.just("start refresh avg");
    }
}
