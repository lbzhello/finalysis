package xyz.liujin.finalysis.analysis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.AvgLineDto;
import xyz.liujin.finalysis.common.service.KLineService;
import xyz.liujin.finalysis.common.service.StockService;

/**
 * 计算均线形态 5 日线， 10 日线， 30 日线
 */
@Service
public class AvgLineService {
    @Autowired
    private KLineService kLineService;

    @Autowired
    private StockService stockService;

    public Flux<AvgLineDto> fiveCroceUp() {
        return Flux.just();
    }
}
