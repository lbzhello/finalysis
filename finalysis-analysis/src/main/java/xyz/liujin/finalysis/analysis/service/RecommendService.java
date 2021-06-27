package xyz.liujin.finalysis.analysis.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.baomidou.mybatisplus.extension.toolkit.ChainWrappers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import xyz.liujin.finalysis.analysis.dto.DailyDataQo;
import xyz.liujin.finalysis.analysis.entity.Recommend;
import xyz.liujin.finalysis.analysis.event.RecommendChangeEvent;
import xyz.liujin.finalysis.analysis.mapper.AnalysisMapper;
import xyz.liujin.finalysis.analysis.mapper.RecommendMapper;
import xyz.liujin.finalysis.base.util.DecimalUtils;
import xyz.liujin.finalysis.base.util.SpringUtils;
import xyz.liujin.finalysis.daily.dto.DailyData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class RecommendService extends ServiceImpl<RecommendMapper, Recommend> implements IService<Recommend> {
    private static final Logger logger = LoggerFactory.getLogger(RecommendService.class);

    @Autowired
    private RecommendMapper recommendMapper;

    @Autowired
    private AnalysisMapper analysisMapper;


    /**
     * 推荐表数据入库
     */
    public Mono<Long> refreshRecommend(LocalDate date, @Nullable List<String> codes) {
        logger.debug("begin save date to table recommend, date {}", date);
        return selectRecommend(date, codes)
                .collectList()
                .flux()
                // 量额从大到小排序
                // 只保留 500 条数据
                .flatMap(recommends -> Flux.fromIterable(recommends.stream()
                        .sorted(Comparator.comparing((Recommend recommend) ->
                                Optional.ofNullable(recommend.getVolAmount()).orElse(BigDecimal.ZERO)).reversed())
                        .collect(Collectors.toList())))
                .take(500)
                // 入库
                .doOnNext(recommend -> recommendMapper.insertOrUpdate(recommend))
                .count()
                .doOnNext(count -> {
                    logger.debug("refreshed recommend table, count {}", count);
                    // 发布推荐表更新事件
                    SpringUtils.getApplicationContext().publishEvent(RecommendChangeEvent.builder()
                            .date(date)
                            .codes(codes)
                            .build());
                });
    }

    /**
     * 查询推荐股票主要信息
     * @param date
     * @return
     */
    public Flux<Recommend> selectRecommend(@Nullable LocalDate date, @Nullable List<String> codes) {
        if (Objects.isNull(date) || CollectionUtil.isEmpty(codes)) {
            return Flux.empty();
        }

        return Flux.create(sink -> {
            List<DailyData> dailyData = analysisMapper.dailyData(DailyDataQo.builder()
                    .date(date)
                    .codes(codes)
                    .build());

            dailyData.forEach(it -> {
                sink.next(Recommend.builder()
                        .date(it.getDate())
                        .stockCode(it.getStockCode())
                        .volAmount(calculateVolAmount(it))
                        .build());
            });

            sink.complete();
        });

    }

    // 计算量额（量比 * 成交额）
    private BigDecimal calculateVolAmount(@Nullable DailyData dailyData) {
        if (Objects.isNull(dailyData)
                || DecimalUtils.isZeroOrNull(dailyData.getVolumeRatio())
                || DecimalUtils.isZeroOrNull(dailyData.getAmount())) {
            return BigDecimal.ZERO;
        }

        return dailyData.getVolumeRatio().multiply(dailyData.getAmount()).setScale(2, RoundingMode.HALF_UP);
    }

    /**
     * 获取最新日期的下一个日期
     * @return
     */
    public LocalDate getNextDate() {
        return getLatestDate().plusDays(1);
    }

    /**
     * 获取数据库最新日期
     * @return
     */
    public LocalDate getLatestDate() {
        return ChainWrappers.lambdaQueryChain(getBaseMapper())
                .orderByDesc(Recommend::getDate)
                .oneOpt()
                .map(Recommend::getDate)
                .orElse(LocalDate.now());
    }
}
