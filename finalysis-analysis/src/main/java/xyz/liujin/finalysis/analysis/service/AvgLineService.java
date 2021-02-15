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
import xyz.liujin.finalysis.analysis.entity.AvgLine;
import xyz.liujin.finalysis.analysis.mapper.AvgLineMapper;
import xyz.liujin.finalysis.analysis.qo.AvgLineQo;
import xyz.liujin.finalysis.common.entity.Stock;
import xyz.liujin.finalysis.common.service.KLineService;
import xyz.liujin.finalysis.common.service.StockService;
import xyz.liujin.finalysis.common.util.DateUtils;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Optional;

/**
 * 计算均线形态 5 日线， 10 日线， 30 日线
 */
@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class AvgLineService extends ServiceImpl<AvgLineMapper, AvgLine> implements IService<AvgLine> {
    Object target;
    private static final Logger logger = LoggerFactory.getLogger(AvgLineService.class);

    @Autowired
    private KLineService kLineService;

    @Autowired
    private StockService stockService;

    /**
     * 获取数据库最新日期
     * @return
     */
    public LocalDate getLatestDate() {
        return getBaseMapper().getLatestDate();
    }

    /**
     * 刷新均线数据
     * 计算均线（5， 10， 20， 30）入库
     */
    public void refreshAvgLine(AvgLineQo avgLineQo) {
        LocalDate start = avgLineQo.getStartDate();
        LocalDate end = avgLineQo.getEndDate();
        // 30 日均线需要需要知道前 30 天的数据
        calculateAvgLine(start.minusDays(30L), end, avgLineQo.getStockCodes())
                // 包括 start 天
                .filter(avgLine -> avgLine.getDate().isAfter(start.minusDays(1)))
                .subscribeOn(Schedulers.boundedElastic())
                .subscribe(this::putByCodeAndDate, e -> logger.error("failed to refreshAvgLine", e));
    }

    /**
     * 计算均线数据
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
                .flatMap(stock -> kLineService.getByCode(stock.getStockCode(), start, end))
                .map(kLineDto -> AvgLine.builder()
                        .stockCode(kLineDto.getStockCode())
                        .date(DateUtils.parseDate(kLineDto.getDate()))
                        .current(new BigDecimal(kLineDto.getClose()))
                        .build())
                .collectList()
                .flux()
                // 计算日均线, 入库
                .flatMap(avgLines -> Flux.create(sink -> {
                    int len = avgLines.size();
                    for (int i = 0; i < len; i++) {
                        AvgLine curAvg = createFrom(avgLines.get(i));
                        curAvg.setAvg5(avg(avgLines, i, 5));
                        curAvg.setAvg10(avg(avgLines, i, 10));
                        curAvg.setAvg20(avg(avgLines, i, 20));
                        curAvg.setAvg30(avg(avgLines, i, 30));
                        sink.next(curAvg);
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

    // 保持不变性
    private AvgLine createFrom(AvgLine avgLine) {
        return AvgLine.builder()
                .stockCode(avgLine.getStockCode())
                .date(avgLine.getDate())
                .current(avgLine.getCurrent())
                .build();
    }

    /**
     * 根据 stockCode 和 date 保存/更新均线数据
     */
    public void putByCodeAndDate(AvgLine avgLine) {
        getQuery().eq(AvgLine::getStockCode, avgLine.getStockCode())
                .eq(AvgLine::getDate, avgLine.getDate())
                .oneOpt()
                .ifPresentOrElse(exist -> {
                    avgLine.setId(exist.getId());
                }, () -> {
                    save(avgLine);
                });
    }

    // 默认返回 2020-01-01 之后的数据
    private LambdaQueryChainWrapper<AvgLine> getQuery() {
        return lambdaQuery().ge(AvgLine::getDate, LocalDate.of(2020, 1, 1));
    }
}
