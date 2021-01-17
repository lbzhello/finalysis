package xyz.liujin.finalysis.spider.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.qo.KLineQo;
import xyz.liujin.finalysis.spider.service.KLineService;

/**
 * 日线数据
 */
@RestController
@RequestMapping("/k")
public class KLineController {
    @Autowired
    private KLineService kLineService;

    @GetMapping
    public Flux<KLineDto> query(@RequestParam(name = "code", required = false) String code) {
        return kLineService.getByCode(KLineQo.builder().code(code).build());
    }

    @GetMapping("{code}/day")
    public Flux<KLineDto> queryDay(@PathVariable(name = "code") String code) {
        return query(code);
    }
}
