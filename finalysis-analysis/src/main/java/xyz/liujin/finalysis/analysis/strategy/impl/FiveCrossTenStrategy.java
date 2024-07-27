package xyz.liujin.finalysis.analysis.strategy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.FiveCrossTenQo;
import xyz.liujin.finalysis.analysis.score.ScoreType;
import xyz.liujin.finalysis.analysis.strategy.ScoreStrategy;
import xyz.liujin.finalysis.daily.service.AvgLineService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component(ScoreType.FIVE_CROSS_TEN)
public class FiveCrossTenStrategy extends ScoreStrategy<FiveCrossTenQo> {
    @Autowired
    private AvgLineService avgLineService;

    @Override
    public Flux<String> findCodes(FiveCrossTenQo fiveCrossTenQo) {
        fiveCrossTenQo = Optional.ofNullable(fiveCrossTenQo).orElse(FiveCrossTenQo.DEFAULT);
        Integer days = Optional.ofNullable(fiveCrossTenQo.getDays()).orElse(3);
        LocalDate date = Optional.ofNullable(fiveCrossTenQo.getDate()).orElse(null);
        List<String> codes = Optional.ofNullable(fiveCrossTenQo.getCodes()).orElse(null);
        return avgLineService.fiveCrossTen(days, date, codes);
    }
}
