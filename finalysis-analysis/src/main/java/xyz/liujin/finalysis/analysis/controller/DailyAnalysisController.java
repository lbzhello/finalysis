package xyz.liujin.finalysis.analysis.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.DailyDateReq;
import xyz.liujin.finalysis.analysis.service.DailyAnalysisService;
import xyz.liujin.finalysis.daily.dto.DailyData;

@Api("股票分析实体类")
@RestController
@RequestMapping("analysis")
public class DailyAnalysisController {
    @Autowired
    private DailyAnalysisService dailyAnalysisService;

    @ApiOperation("获取股票日数据")
    @PostMapping("daily")
    public Flux<DailyData> dailyData(@RequestBody DailyDateReq req) {
        return dailyAnalysisService.dailyData(req);
    }
}
