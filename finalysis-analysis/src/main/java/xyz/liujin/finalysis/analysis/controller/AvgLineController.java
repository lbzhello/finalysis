package xyz.liujin.finalysis.analysis.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.AvgLineDto;
import xyz.liujin.finalysis.analysis.service.AvgLineService;

@RestController
@RequestMapping("average")
public class AvgLineController {
    @Autowired
    private AvgLineService avgLineService;

    /**
     * 获取 5 日线上穿形态的股票
     * @return
     */
    @GetMapping("five-croce-up")
    public Flux<AvgLineDto> fiveCroceUp() {
        return avgLineService.fiveCroceUp();
    }
}
