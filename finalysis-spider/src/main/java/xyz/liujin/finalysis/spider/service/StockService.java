package xyz.liujin.finalysis.spider.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xyz.liujin.finalysis.common.entity.Stock;
import xyz.liujin.finalysis.common.mapper.StockMapper;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class StockService extends ServiceImpl<StockMapper, Stock> implements IService<Stock> {
}
