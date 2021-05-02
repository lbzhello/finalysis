package xyz.liujin.finalysis.start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.daily.entity.DayAvgLine;
import xyz.liujin.finalysis.daily.mapper.AvgLineMapper;
import xyz.liujin.finalysis.daily.service.AvgLineService;

import java.math.BigDecimal;
import java.time.LocalDate;

@RestController
@RequestMapping("test")
public class TestController {

    @Autowired
    private AvgLineMapper avgLineMapper;
    @GetMapping("getAvgLatestDate")
    public LocalDate getAvgLatestDate() {
        return avgLineMapper.getLatestDate();
    }

    @Autowired
    private AvgLineService avgLineService;
    @GetMapping("calculateAvgLine")
    public String calculateAvgLine(@RequestParam(name = "start", required = false) LocalDate start) {
        avgLineService.calculateAvgLine(start, null, null)
                .subscribe(it -> {
                    System.out.println(it);
                });
        return "success";
    }

    @GetMapping("save-avg-line")
    public void saveAvgLine() {
        avgLineService.saveBatchByCodeDate(Flux.just(DayAvgLine.builder()
                .stockCode("8577")
                .date(LocalDate.now())
                .current(BigDecimal.valueOf(88))
                .avg5(BigDecimal.valueOf(5))
                .avg10(BigDecimal.valueOf(10))
                .avg20(BigDecimal.valueOf(20))
                .avg30(BigDecimal.valueOf(30))
                .build()));
    }
}
