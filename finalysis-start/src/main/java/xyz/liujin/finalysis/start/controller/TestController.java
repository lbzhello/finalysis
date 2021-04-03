package xyz.liujin.finalysis.start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import xyz.liujin.finalysis.daily.mapper.AvgLineMapper;
import xyz.liujin.finalysis.daily.service.AvgLineService;

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
}
