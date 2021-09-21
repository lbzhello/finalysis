package xyz.liujin.finalysis.analysis.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import xyz.liujin.finalysis.analysis.entity.StockTag;
import xyz.liujin.finalysis.analysis.mapper.StockTagMapper;
import xyz.liujin.finalysis.base.util.MyLogger;

import java.time.LocalDate;

@Service
public class StockTagService extends ServiceImpl<StockTagMapper, StockTag> implements IService<StockTag> {
    private static final MyLogger logger = MyLogger.getLogger(StockTagService.class);

    public void deleteByDateAndTag(LocalDate date, String tag) {
        getBaseMapper().deleteByDateAndTag(date, tag);
    }

    public void saveIfNotExist(StockTag stockTag) {
        lambdaQuery().eq(StockTag::getDate, stockTag.getDate())
                .eq(StockTag::getStockCode, stockTag.getStockCode())
                .eq(StockTag::getTag, stockTag.getTag())
                .oneOpt()
                // 存在忽略，不存在新增
                .ifPresentOrElse(it -> {}, () -> {
                    save(stockTag);
                });
    }
}
