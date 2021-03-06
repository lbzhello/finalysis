package xyz.liujin.finalysis.common.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.common.converter.KLineConverter;
import xyz.liujin.finalysis.common.dto.KLineDto;
import xyz.liujin.finalysis.common.entity.KLine;
import xyz.liujin.finalysis.common.mapper.KLineMapper;

import java.time.LocalDate;
import java.util.Objects;
import java.util.Optional;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class KLineService extends ServiceImpl<KLineMapper, KLine> implements IService<KLine> {

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

    // 数据在不同的分区，默认返回 2020-01-01 之后的数据
    private LambdaQueryChainWrapper<KLine> getQuery() {
        return lambdaQuery().ge(KLine::getDate, LocalDate.of(2020, 1, 1));
    }
}
