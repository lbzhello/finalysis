package xyz.liujin.finalysis.analysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.service.AvgLineService;

import java.util.Objects;

@RestController
@RequestMapping("avg")
public class AvgLineController {
    @Autowired
    private AvgLineService avgLineService;

    /**
     * 重新计算均线并入库
     * @return
     */
    @GetMapping("/refresh")
    public Flux<String> refresh(@RequestParam(name = "code", required = false) String code) {
        if (Objects.nonNull(code)) {
            avgLineService.refreshAvgLine(code);
        } else {
            avgLineService.refreshAvgLine();
        }
        return Flux.just("start refresh avg");
    }
}
