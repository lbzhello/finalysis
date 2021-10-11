package xyz.liujin.finalysis.analysis.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.analysis.dto.IncreaseRatioQo;
import xyz.liujin.finalysis.analysis.dto.MinimumPriceSupportQo;
import xyz.liujin.finalysis.analysis.dto.TurnRatioQo;
import xyz.liujin.finalysis.analysis.entity.StockScore;
import xyz.liujin.finalysis.analysis.service.StockScoreService;
import xyz.liujin.finalysis.analysis.strategy.StrategyQo;
import xyz.liujin.finalysis.analysis.strategy.impl.TurnRatioStrategy;
import xyz.liujin.finalysis.base.executor.TaskPool;
import xyz.liujin.finalysis.base.page.PageQo;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

@Api("股票计分接口")
@RestController
@RequestMapping("score")
public class ScoreController {

    @Autowired
    private StockScoreService stockScoreService;

    @ApiOperation("股票计分")
    @GetMapping
    public Flux<String> score() {
        List<StrategyQo> strategies = new ArrayList<>();

        // 换手比 3 5
        strategies.add(TurnRatioQo.builder()
                .recDays(3)
                .hisDays(5)
                .minRatio(BigDecimal.valueOf(2))
                .minAvgAmount(BigDecimal.valueOf(1e8))
                .page(PageQo.builder()
                        .orderBy("turn_ratio desc")
                        .limit(100)
                        .build())
                .build());
        // 换手比 9 9
        strategies.add(TurnRatioQo.builder()
                .recDays(9)
                .hisDays(9)
                .minAvgAmount(BigDecimal.valueOf(1e8))
                .page(PageQo.builder()
                        .orderBy("turn_ratio desc")
                        .limit(100)
                        .build())
                .build());
        // 增幅比
        strategies.add(IncreaseRatioQo.builder()
                .recDays(3)
                .hisDays(5)
                .minRatio(BigDecimal.valueOf(2))
                .page(PageQo.builder()
                        .orderBy("pct_change_ratio desc")
                        .limit(100)
                        .build())
                .build());
        // 最低价支撑
        strategies.add(MinimumPriceSupportQo.builder()
                .recDays(3)
                .build());

        // 计分入库
        stockScoreService.score(Flux.fromIterable(strategies))
                .subscribeOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                .subscribe();

        return Flux.just("start to score background...");
    }

    @Autowired
    private TurnRatioStrategy turnRatioStrategy;
    @PostMapping("turn-ratio")
    public Flux<StockScore> turnRatioStrategy(@RequestBody TurnRatioQo turnRatioQo) {
        return turnRatioStrategy.score(turnRatioQo);
    }

}
