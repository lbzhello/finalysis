package xyz.liujin.finalysis.start.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import xyz.liujin.finalysis.analysis.mapper.AvgLineMapper;

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
}
