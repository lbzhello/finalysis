package xyz.liujin.finalysis.daily.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import xyz.liujin.finalysis.base.util.DateUtils;
import xyz.liujin.finalysis.daily.entity.DailyIndicator;
import xyz.liujin.finalysis.daily.mapper.DailyIndicatorMapper;

import java.time.LocalDate;
import java.util.Optional;

/**
 * 计算均线形态 5 日线， 10 日线， 30 日线
 */
@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class DailyIndicatorService extends ServiceImpl<DailyIndicatorMapper, DailyIndicator> implements IService<DailyIndicator> {
    /**
     * 获取最新日期的下一个日期
     * @return
     */
    public LocalDate getNextDate() {
        return getLatestDate().plusDays(1);
    }

    /**
     * 获取数据库最新的日期，默认年初第一天
     * @return
     */
    public LocalDate getLatestDate() {
        return Optional.ofNullable(getBaseMapper().getLatestDate()).orElse(DateUtils.beginOfYear());
    }

    /**
     * 根据股票代码和日期保存数据，如果存在则更新
     * @param dailyIndicator
     */
    public void saveByCodeDate(DailyIndicator dailyIndicator) {
        getBaseMapper().saveByCodeDate(dailyIndicator);
    }
}
