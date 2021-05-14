package xyz.liujin.finalysis.daily.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.publisher.FluxSink;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.base.executor.TaskPool;
import xyz.liujin.finalysis.daily.converter.KLineConverter;
import xyz.liujin.finalysis.daily.dto.KLineDto;
import xyz.liujin.finalysis.daily.entity.KLine;
import xyz.liujin.finalysis.daily.mapper.KLineMapper;
import xyz.liujin.finalysis.daily.qo.KLineQo;
import xyz.liujin.finalysis.stock.entity.Stock;
import xyz.liujin.finalysis.stock.service.StockService;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Collectors;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class KLineService extends ServiceImpl<KLineMapper, KLine> implements IService<KLine> {
    /**
     * 分页查询
     * @return
     */
    public Flux<KLine> pageQuery(KLineQo kLineQo) {
        List<KLine> kLines = getBaseMapper().pageQuery(kLineQo);
        if (CollectionUtil.isEmpty(kLines)) {
            return Flux.just();
        }
        return Flux.fromIterable(kLines);
    }

    /**
     * 获取数据库最新数据日期
     * @return
     */
    public LocalDate getLatestDate() {
        return getBaseMapper().getLatestDate();
    }

    /**
     * 获取数据库最新日期的下一个日期
     * @return
     */
    public @Nullable LocalDate getNextDate() {
        return Optional.ofNullable(getLatestDate()).map(it -> it.plusDays(1)).orElse(null);
    }

    /**
     * 根据股票代码获取股票日 k 数据
     * 默认获取 2021-01-01 以后的数据
     * 根据日期倒叙排列
     * @param stockCode
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return
     */
    public Flux<KLineDto> findByCodeAndOrderByDateDesc(String stockCode, @Nullable LocalDate startDate, @Nullable LocalDate endDate) {
        return Flux.fromIterable(lambdaQuery()
                .eq(KLine::getStockCode, stockCode)
                .ge(Objects.nonNull(startDate), KLine::getDate, startDate)
                .le(Objects.nonNull(endDate), KLine::getDate, endDate)
                .orderByDesc(KLine::getDate)
                .list())
                .map(KLineConverter::toKLineDto);
    }

    /**
     * 保存或更新
     * @param kLineDto
     * @deprecated 被 {@link #insertOrUpdate(KLine)} 方法取代
     */
    @Deprecated
    public void saveOrUpdate(KLineDto kLineDto) {
        KLine kLine = KLineConverter.toKLine(kLineDto);
        // 同一股票，同一时间不重复
        lambdaQuery().eq(KLine::getStockCode, kLine.getStockCode())
                .eq(KLine::getDate, kLine.getDate())
                .oneOpt()
                .ifPresentOrElse(exist -> {
                    kLine.setId(exist.getId());
                    updateById(kLine);
                }, () -> {
                    save(kLine);
                });
        saveOrUpdate(kLine);
    }

    /**
     * 新增或更新
     * @param kLine
     */
    public void insertOrUpdate(KLine kLine) {
        getBaseMapper().insertOrUpdate(kLine);
    }

    // 数据在不同的分区，默认返回 2020-01-01 之后的数据
    public LambdaQueryChainWrapper<KLine> getQuery() {
        return lambdaQuery().ge(KLine::getDate, LocalDate.of(2020, 1, 1));
    }

    @Autowired
    private StockService stockService;

    /**
     * 计算股票量比
     * @param start 开始日期，默认当天
     * @param end 结束日期，默认当天
     * @param codes 股票列表，默认所有
     */
    public void calculateVolumeRatio(@Nullable LocalDate start, @Nullable LocalDate end, @Nullable List<String> codes) {
        // 量比需要计算 5 日前的数据，考虑到节假日，这里直接取前 30 天(窝慧诰愫尼妹柚劫价䒤荟炒锅30兲码)
        LocalDate start0 = Optional.ofNullable(start).orElse(LocalDate.now());
        LocalDate _start = start0.minusDays(30);
        LocalDate _end = Optional.ofNullable(end).orElse(LocalDate.now());
        List<String> _codes = CollectionUtil.isNotEmpty(codes) ? codes : stockService.list().stream()
                .map(Stock::getStockCode).collect(Collectors.toList());

        Flux.fromIterable(_codes)
                .map(code -> getQuery()
                        .eq(KLine::getStockCode, code)
                        .ge(KLine::getDate, _start)
                        .le(KLine::getDate, _end)
                        .orderByDesc(KLine::getDate)
                        .list())
                .subscribeOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                .flatMap(kLines -> Flux.create((Consumer<FluxSink<KLine>>) sink -> {
                    // 计算量比，入库
                    for (int i = 0; i < kLines.size(); i++) {
                        KLine kLine = kLines.get(i);
                        // 只计算需要的天数
                        if (!kLine.getDate().isBefore(start0)) {
                            KLine newKLine = new KLine();
                            newKLine.setId(kLine.getId());
                            newKLine.setVolumeRatio(volumeRatio(i, kLines));
                            sink.next(newKLine);
                        }

                    }
                    sink.complete();
                }))
                .collectList()
                .subscribe(kLines -> {
                    updateBatchById(kLines, 100);
                });

    }

    /**
     * 计算第 index 个元素的量比。第 index 个元素与前 5 个元素的均值的比值
     * @param index
     * @param kLines
     */
    private BigDecimal volumeRatio(int index, List<KLine> kLines) {
        if (index < 0 || index > kLines.size() - 1) {
            throw new IllegalArgumentException("failed calculate volume ratio: index out of range");
        }

        // 需要计算的元素个数，最多 5 个
        int len = Math.min(5, kLines.size() - 1 - index);

        Long acc = 0L;
        for (int i = 1; i <= len; i++) {
            KLine kLine = kLines.get(index + i);
            acc += kLine.getVolume();
        }

        // 初始元素量比默认给 1
        if (len == 0) {
            return BigDecimal.ONE;
        }

        BigDecimal avg = BigDecimal.valueOf(acc).divide(BigDecimal.valueOf(len), 4, RoundingMode.HALF_EVEN);

        if (BigDecimal.ZERO.compareTo(avg) == 0) {
            return BigDecimal.ONE;
        }

        return BigDecimal.valueOf(kLines.get(index).getVolume()).divide(avg, 4, RoundingMode.HALF_EVEN);
    }
}
