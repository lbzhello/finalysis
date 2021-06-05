package xyz.liujin.finalysis.analysis.service;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.extension.service.IService;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.lang.Nullable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Isolation;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Flux;
import reactor.core.scheduler.Schedulers;
import xyz.liujin.finalysis.analysis.dto.DailyDataQo;
import xyz.liujin.finalysis.analysis.entity.Recommend;
import xyz.liujin.finalysis.analysis.mapper.AnalysisMapper;
import xyz.liujin.finalysis.analysis.mapper.RecommendMapper;
import xyz.liujin.finalysis.base.executor.TaskPool;
import xyz.liujin.finalysis.base.util.DecimalUtils;
import xyz.liujin.finalysis.daily.dto.DailyData;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;

@Service
@Transactional(isolation = Isolation.DEFAULT, propagation = Propagation.REQUIRED, timeout = 3*60, rollbackFor = Exception.class)
public class RecommendService extends ServiceImpl<RecommendMapper, Recommend> implements IService<Recommend> {
    @Autowired
    private RecommendMapper recommendMapper;

    @Autowired
    private AnalysisMapper analysisMapper;


    /**
     * 保存或更新
     */
    public void saveAsync(LocalDate date, @Nullable List<String> codes) {
        selectRecommend(date, codes)
                .subscribeOn(Schedulers.fromExecutor(TaskPool.getInstance()))
                .subscribe(recommend -> {
                    recommendMapper.insertOrUpdate(recommend);
                });
    }

    /**
     * 查询推荐股票主要信息
     * @param date
     * @return
     */
    public Flux<Recommend> selectRecommend(@Nullable LocalDate date, @Nullable List<String> codes) {
        if (Objects.isNull(date) || CollectionUtil.isEmpty(codes)) {
            return Flux.empty();
        }

        return Flux.create(sink -> {
            List<DailyData> dailyData = analysisMapper.dailyData(DailyDataQo.builder()
                    .date(date)
                    .codes(codes)
                    .build());

            dailyData.forEach(it -> {
                sink.next(Recommend.builder()
                        .date(it.getDate())
                        .stockCode(it.getStockCode())
                        .volAmount(calculateVolAmount(it))
                        .build());
            });

            sink.complete();
        });

    }

    // 计算量额（量比 * 成交额）
    private BigDecimal calculateVolAmount(@Nullable DailyData dailyData) {
        if (Objects.isNull(dailyData)
                || DecimalUtils.isZeroOrNull(dailyData.getVolumeRatio())
                || DecimalUtils.isZeroOrNull(dailyData.getAmount())) {
            return BigDecimal.ZERO;
        }

        return dailyData.getVolumeRatio().multiply(dailyData.getAmount()).setScale(2, RoundingMode.HALF_UP);
    }
}
