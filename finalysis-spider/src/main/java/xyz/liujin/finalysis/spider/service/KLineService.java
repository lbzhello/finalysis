package xyz.liujin.finalysis.spider.service;

import com.baomidou.mybatisplus.extension.conditions.query.LambdaQueryChainWrapper;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.spider.converter.KLineConverter;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.entity.KLine;
import xyz.liujin.finalysis.spider.mapper.KLineMapper;
import xyz.liujin.finalysis.spider.qo.KLineQo;

import java.time.LocalDate;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class KLineService extends ServiceImpl<KLineMapper, KLine> implements IService<KLine> {

    public Flux<KLineDto> getByCode(KLineQo qo) {
        return Flux.create(sink -> {
            getQuery().eq(KLine::getStockCode, qo.getCode()).list()
                    .stream()
                    .map(KLineConverter::toKLineDto)
                    .forEach(sink::next);
            sink.complete();
        });
    }

    public void saveOrUpdate(KLineDto kLineDto) {
        KLine kLine = KLineConverter.toKLine(kLineDto);
        // 同一股票，同一时间不重复
        lambdaQuery().eq(KLine::getStockCode, kLine.getStockCode())
                .eq(KLine::getDate, kLine.getDate())
                .oneOpt()
                .ifPresent(exist -> {
                    kLine.setId(exist.getId());
                });
        saveOrUpdate(kLine);
    }

    // 数据在不同的分区，默认返回 2020-01-01 之后的数据
    private LambdaQueryChainWrapper<KLine> getQuery() {
        return lambdaQuery().ge(KLine::getDate, LocalDate.of(2020, 1, 1));
    }
}
