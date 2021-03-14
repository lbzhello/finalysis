package xyz.liujin.finalysis.analysis.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.analysis.dto.DayAvgLine;
import xyz.liujin.finalysis.analysis.entity.AvgLine;
import xyz.liujin.finalysis.analysis.mapper.AvgLineMapper;
import xyz.liujin.finalysis.analysis.qo.AvgLineQo;
import xyz.liujin.finalysis.base.entity.Stock;
import xyz.liujin.finalysis.base.schedule.TaskPool;
import xyz.liujin.finalysis.base.service.KLineService;
import xyz.liujin.finalysis.base.service.StockService;
import xyz.liujin.finalysis.base.util.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Comparator;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 计算均线形态 5 日线， 10 日线， 30 日线
 */
@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class AvgLineService extends ServiceImpl<AvgLineMapper, AvgLine> implements IService<AvgLine> {
    Object target;
    private static final Logger logger = LoggerFactory.getLogger(AvgLineService.class);

    // 均线天数
    private List<Integer> DAYS = List.of(5, 10, 20, 30);

    @Autowired
    private KLineService kLineService;

    @Autowired
    private StockService stockService;

    /**
     * 获取 5 日线突破十日线的股票
     * @param days 突破天数，最多十天
     * @return
     */
    public Flux<String> fiveCrossTen(Integer days) {
        int _days = Math.min(days, 10);
        // 当前天数 5 日线大于等于 10 日线
        LocalDate end = Optional.ofNullable(getLatestDate())
                .orElse(LocalDate.now());
        Flux<DayAvgLine> endDayAvg = Flux.fromIterable(
                getBaseMapper().findDayAvg(AvgLineQo.builder()
                        .start(end)
                        .end(end)
                        .build()))
                .filter(avg -> avg.getAvg5().compareTo(avg.getAvg10()) >= 0);

        // 起始天数 5 日线小于等于 10 日线
        LocalDate start = end.minusDays(_days);
        Flux<DayAvgLine> startDayAvg = Flux.fromIterable(
                getBaseMapper().findDayAvg(AvgLineQo.builder()
                        .start(start)
                        .end(start)
                        .build()))
                .filter(avg -> avg.getAvg5().compareTo(avg.getAvg10()) <= 0);

        // 取交集
        return Flux.zip(startDayAvg.collectList(), endDayAvg.collectList())
                .flatMap(tuple -> {
                    Set<String> t1Set = tuple.getT1().stream()
                            .map(DayAvgLine::getStockCode).collect(Collectors.toSet());

                    Set<String> t2Set = tuple.getT2().stream()
                            .map(DayAvgLine::getStockCode).collect(Collectors.toSet());

                    t2Set.retainAll(t1Set);

                    return Flux.fromIterable(t2Set);
                });
    }

    /**
     * 获取上升趋势的均线，即从最新的数据开始， 5 日线大于 10 日线的天数
     * @param days 连续递增天数
     * @return
     */
    public Flux<String> upwards(Integer days) {
        LocalDate start = Optional
                .ofNullable(getLatestDate())
                .map(it -> it.minusDays(days - 1))
                .orElse(LocalDate.now());

        return Flux.fromIterable(getBaseMapper().upwards(start));
    }

    /**
     * 获取数据库最新日期
     * @return
     */
    public LocalDate getLatestDate() {
        return getBaseMapper().getLatestDate();
    }

    /**
     * 获取最新日期的下一个日期
     * @return
     */
    @Nullable
    public LocalDate getNextDate() {
        return Optional.ofNullable(getLatestDate()).map(it -> it.plusDays(1)).orElse(null);
    }

    /**
     * 刷新均线数据
     * 计算均线（5， 10， 20， 30）入库
     */
    public void refreshAvgLine(@Nullable LocalDate startDate, @Nullable LocalDate endDate, @Nullable List<String> codes) {
        // 开始时间，默认数据库获取最新的日期
        LocalDate start = Optional.ofNullable(startDate).orElseGet(() -> Optional.
                ofNullable(this.getNextDate()).orElse(LocalDate.now()));

        // 结束时间，默认当日
        LocalDate end = Optional.ofNullable(endDate).orElse(LocalDate.now());

        // 均线需要需要知道前 n 天的数据；因为有节假日，这里乘以 3 粗略的估算
        int n = DAYS.stream().max(Comparator.comparingInt(Integer::intValue)).map(it -> it*3).orElse(0);
        calculateAvgLine(start.minusDays(n), end, codes)
                // 包括 start 天
                .filter(avgLine -> avgLine.getDate().isAfter(start.minusDays(1)))
                .window(100)
                .subscribeOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                .subscribe(avgFlux -> avgFlux
                                .collectList()
                                .subscribe(this::saveBatchByCodeDateStatistic), e -> logger.error("failed to refreshAvgLine", e));
    }



    /**
     * 计算均线数据
     *
     * @param startDate 开始日期，默认年初
     * @param endDate  结束日期，默认当天
     * @param codes 需要计算的股票
     * @return
     */
    public Flux<AvgLine> calculateAvgLine(@Nullable LocalDate startDate, @Nullable LocalDate endDate, @Nullable List<String> codes) {
        // 股票
        Flux<Stock> stockFlux;
        if (CollectionUtil.isNotEmpty(codes)) {
            stockFlux = stockService.queryByCodes(codes);
        } else { // 默认计算所有股票均线
            stockFlux = stockService.queryAll();
        }

        // 开始日期，默认年初
        LocalDate start = Optional.ofNullable(startDate).orElse(DateUtils.beginOfYear());

        // 结束日期，默认当天
        LocalDate end = Optional.ofNullable(endDate).orElse(LocalDate.now());

        return stockFlux
                .flatMap(stock -> kLineService.findByCodeAndOrderByDateDesc(stock.getStockCode(), start, end))
                .map(kLineDto -> AvgLine.builder()
                        .stockCode(kLineDto.getStockCode())
                        .date(DateUtils.parseDate(kLineDto.getDate()))
                        .current(new BigDecimal(kLineDto.getClose()))
                        .build())
                .collectList()
                .flux()
                // 计算日均线
                .flatMap(avgLines -> Flux.create(sink -> {
                    int len = avgLines.size();
                    for (int i = 0; i < len; i++) {
                        // 计算 day 日均线
                        for (Integer day : DAYS) {
                            AvgLine curAvg = createFrom(avgLines.get(i));
                            curAvg.setStatistic(day);
                            curAvg.setAvg(avg(avgLines, i, day));
                            sink.next(curAvg);
                        }
                    }
                    sink.complete();
                }));
    }


    // 计算 avgLines 中 从 start 开始的连续 days 个均值
    private BigDecimal avg(List<AvgLine> avgLines, int start, int days) {
        int len = Math.min(start + days, avgLines.size());

        BigDecimal acc = BigDecimal.ZERO;
        for (int i = start; i < len; i++) {
            acc = acc.add(avgLines.get(i).getCurrent());
        }
        // days 天数均值
        return acc.divide(BigDecimal.valueOf(days), 4, RoundingMode.HALF_EVEN);
    }

    // 复制，保持不变性
    private AvgLine createFrom(AvgLine avgLine) {
        return AvgLine.builder()
                .stockCode(avgLine.getStockCode())
                .date(avgLine.getDate())
                .current(avgLine.getCurrent())
                .build();
    }

    /**
     * 根据 stockCode, date, days 保存/更新均线数据
     */
    public void saveBatchByCodeDateStatistic(List<AvgLine> avgLines) {
        getBaseMapper().saveBatchByCodeDateStatistic(avgLines);
    }

    // 默认返回 2020-01-01 之后的数据
    private LambdaQueryChainWrapper<AvgLine> getQuery() {
        return lambdaQuery().ge(AvgLine::getDate, LocalDate.of(2020, 1, 1));
    }
}
