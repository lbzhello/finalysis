package xyz.liujin.finalysis.analysis.controller;

import io.swagger.annotations.ApiOperation;
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
import java.util.Collections;
import java.util.Optional;

@RestController
@RequestMapping("avg")
public class AvgLineController {
    @Autowired
    private AvgLineService avgLineService;

    /**
     * @param start 开始日期 yyyy-MM-dd
     * @param end 结束日期 yyyy-MM-dd
     * @param code 股票代码
     * @return
     */
    @ApiOperation("更新均线并入库")
    @GetMapping("/refresh")
    public Flux<String> refresh(
            @RequestParam(name = "start", required = false) String start,
            @RequestParam(name = "end", required = false) String end,
            @RequestParam(name = "code", required = false) String code) {
        AvgLineQo qo = AvgLineQo.builder()
                .startDate(Optional.ofNullable(start).map(DateUtils::parseDate).orElse(LocalDate.now()))
                .endDate(Optional.ofNullable(end).map(DateUtils::parseDate).orElse(LocalDate.now()))
                .stockCodes(Optional.ofNullable(code).map(Collections::singletonList).orElse(Collections.emptyList()))
                .build();
        avgLineService.refreshAvgLine(qo);
        return Flux.just("start refresh avg");
    }
}
