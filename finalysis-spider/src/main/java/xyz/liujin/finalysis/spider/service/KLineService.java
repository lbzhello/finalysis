package xyz.liujin.finalysis.spider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xyz.liujin.finalysis.spider.converter.KLineConverter;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.entity.KLine;
import xyz.liujin.finalysis.spider.mapper.KLineMapper;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class KLineService extends ServiceImpl<KLineMapper, KLine> implements IService<KLine> {
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
}
