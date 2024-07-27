package xyz.liujin.finalysis.analysis.strategy.impl;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.analysis.dto.FiveAboveTenQo;
import xyz.liujin.finalysis.analysis.score.ScoreType;
import xyz.liujin.finalysis.analysis.strategy.ScoreStrategy;
import xyz.liujin.finalysis.daily.service.AvgLineService;

import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

@Component(ScoreType.FIVE_ABOVE_TEN)
public class FiveAboveTenStrategy extends ScoreStrategy<FiveAboveTenQo> {
    @Autowired
    private AvgLineService avgLineService;

    @Override
    public Flux<String> findCodes(FiveAboveTenQo fiveAboveTenQo) {
        fiveAboveTenQo = Optional.ofNullable(fiveAboveTenQo).orElse(FiveAboveTenQo.DEFAULT);
        Integer days = Optional.ofNullable(fiveAboveTenQo.getDays()).orElse(3);
        LocalDate date = Optional.ofNullable(fiveAboveTenQo.getDate()).orElse(null);
        List<String> codes = Optional.ofNullable(fiveAboveTenQo.getCodes()).orElse(null);
        return avgLineService.fiveAboveTen(days, date, codes);
    }
}
