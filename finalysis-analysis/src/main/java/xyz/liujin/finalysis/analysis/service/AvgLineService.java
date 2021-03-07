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
import java.util.Objects;
import java.util.Optional;

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
     * 获取上升趋势的均线
     * @return
     */
    public Flux<DayAvgLine> upwards(AvgLineQo avgLineQo) {
        // 日期默认最后一个交易日
        if (Objects.isNull(avgLineQo.getStart()) || Objects.isNull(avgLineQo.getEnd())) {
            LocalDate latest = getLatestDate();
            if (Objects.isNull(avgLineQo.getStart())) {
                avgLineQo.setStart(latest);
            }

            if (Objects.isNull(avgLineQo.getEnd())) {
                avgLineQo.setEnd(latest);
            }
        }
        List<DayAvgLine> dayAvg = getBaseMapper().findDayAvg(avgLineQo);
        return Flux.fromIterable(dayAvg)
                .filter(avg -> avg.getAvg5().compareTo(avg.getAvg10()) > 0);

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
    public @Nullable LocalDate getNextDate() {
        return Optional.ofNullable(getLatestDate()).map(it -> it.plusDays(1)).orElse(null);
    }

    /**
     * 刷新均线数据
     * 计算均线（5， 10， 20， 30）入库
     */
    public void refreshAvgLine(LocalDate start, LocalDate end, List<String> codes) {
        // 均线需要需要知道前 n 天的数据；因为有节假日，这里乘以 3 粗略的估算
        int n = DAYS.stream().max(Comparator.comparingInt(Integer::intValue)).map(it -> it*3).orElse(0);
        calculateAvgLine(start.minusDays(n), end, codes)
                // 包括 start 天
                .filter(avgLine -> avgLine.getDate().isAfter(start.minusDays(1)))
                .window(100)
                .subscribeOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                .subscribe(avgFlux -> avgFlux
                                .collectList()
                                .subscribe(this::saveBatchByCodeDateCount), e -> logger.error("failed to refreshAvgLine", e));
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
                            curAvg.setCount(day);
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
    public void saveBatchByCodeDateCount(List<AvgLine> avgLines) {
        getBaseMapper().saveBatchByCodeDateCount(avgLines);
    }

    // 默认返回 2020-01-01 之后的数据
    private LambdaQueryChainWrapper<AvgLine> getQuery() {
        return lambdaQuery().ge(AvgLine::getDate, LocalDate.of(2020, 1, 1));
    }
}
