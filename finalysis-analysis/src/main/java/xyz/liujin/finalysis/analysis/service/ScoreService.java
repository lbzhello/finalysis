package xyz.liujin.finalysis.analysis.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.analysis.dto.IncreaseRatioQo;
import xyz.liujin.finalysis.analysis.dto.ScoreQo;
import xyz.liujin.finalysis.analysis.entity.StockTag;
import xyz.liujin.finalysis.analysis.entity.TagScore;
import xyz.liujin.finalysis.analysis.strategy.IncreaseRatioStrategy;
import xyz.liujin.finalysis.base.util.MyLogger;
import xyz.liujin.finalysis.base.util.ObjectUtils;
import xyz.liujin.finalysis.daily.service.KLineService;

import java.time.LocalDate;
import java.util.Objects;

/**
 * 图片计分
 */
@Service
@Transactional
public class ScoreService {
    private static final MyLogger logger = MyLogger.getLogger(ScoreService.class);

    @Autowired
    private KLineService kLineService;

    @Autowired
    private IncreaseRatioStrategy increaseRatioStrategy;

    @Autowired
    private StockTagService stockTagService;

    @Autowired
    private TagScoreService tagScoreService;

    /**
     * 股票统计分数
     * @return
     */
    public Mono<Integer> score(ScoreQo scoreQo) {
        logger.debug("start to score", "scoreQO", scoreQo);
        // 推荐日期，默认数据库最新或当天
        LocalDate date = ObjectUtils.firstNonNull(scoreQo.getDate(), kLineService.getLatestDate(), LocalDate.now());

        // 增幅比指标
        IncreaseRatioQo increaseRatio = scoreQo.getIncreaseRatio();
        if (Objects.nonNull(increaseRatio) && Objects.isNull(increaseRatio.getDate())) {
            increaseRatio.setDate(date);
        }
        // 默认全局分页信息
        if (Objects.isNull(increaseRatio.getPage()) && Objects.nonNull(scoreQo.getPage())) {
            increaseRatio.setPage(scoreQo.getPage());
        }

        // 计分入库
        logger.debug("score increase ratio strategy", "increaseRatio", increaseRatio);

        // 标签分数入库
        TagScore tag = tagScoreService.getTag(increaseRatio);

        // 删除当日，具有该标签的股票，因为每次计分输出的股票是不一样的
        stockTagService.deleteByDateAndTag(date, tag.getTag());

        increaseRatioStrategy.findCodes(increaseRatio)
                .map(stockCode -> {
                    return StockTag.builder()
                            .date(date)
                            .stockCode(stockCode)
                            .tag(tag.getTag())
                            .build();
                })
                .map(stockTags -> {
                    stockTagService.save(stockTags);
                    return 1;
                })
                .reduce(Integer::sum)
                .subscribe(count -> logger.debug("increaseRatioStrategy", "count", count),
                        e -> logger.error("failed to score increaseRatio", e));
        return Mono.empty();
    }

}
