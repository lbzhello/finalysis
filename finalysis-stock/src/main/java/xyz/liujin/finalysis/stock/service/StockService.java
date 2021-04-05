package xyz.liujin.finalysis.stock.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.github.benmanes.caffeine.cache.CacheLoader;
import com.github.benmanes.caffeine.cache.Caffeine;
import com.github.benmanes.caffeine.cache.LoadingCache;
import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.stock.entity.Stock;
import xyz.liujin.finalysis.stock.mapper.StockMapper;

import java.time.Duration;
import java.util.Collection;
import java.util.List;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class StockService extends ServiceImpl<StockMapper, Stock> implements IService<Stock> {
    private final LoadingCache<String, Stock> cache = Caffeine.newBuilder()
            .expireAfterWrite(Duration.ofSeconds(10*60))
            .maximumSize(10000)
            .build(stockCacheLoader());

    @Bean
    public ApplicationRunner initCache() {
        return it -> {
            // 初始化缓存
            List<Stock> stocks = lambdaQuery().list();
            for (Stock stock : stocks) {
                cache.put(stock.getStockCode(), stock);
            }
        };
    }

    private CacheLoader<String, Stock> stockCacheLoader() {
        return this::getById;
    }

    /**
     * 根据 Id 获取值
     * @param code
     * @return
     */
    public Stock selectById(String code) {
        return cache.get(code);
    }

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
