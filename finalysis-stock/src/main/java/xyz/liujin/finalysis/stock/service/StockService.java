package xyz.liujin.finalysis.stock.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.stock.entity.Stock;
import xyz.liujin.finalysis.stock.mapper.StockMapper;

import java.util.Collection;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class StockService extends ServiceImpl<StockMapper, Stock> implements IService<Stock> {
    public Flux<Stock> queryByCodes(Collection<String> codes) {
        if (CollectionUtil.isEmpty(codes)) {
            return Flux.just();
        }
        return Flux.fromIterable(lambdaQuery().in(Stock::getStockCode, codes).list());
    }

    public Flux<Stock> queryAll() {
        return Flux.fromIterable(lambdaQuery().list());
    }
}
