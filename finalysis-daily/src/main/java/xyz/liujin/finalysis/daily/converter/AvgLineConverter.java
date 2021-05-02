package xyz.liujin.finalysis.daily.converter;

import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.daily.entity.AvgLine;
import xyz.liujin.finalysis.daily.entity.DayAvgLine;

/**
 * 均线视图转换
 * 转换成具体的数据库
 */
public class AvgLineConverter {
    public static Flux<AvgLine> toAvgLine(DayAvgLine dayAvgLine) {
        return Flux.create(avgLineFluxSink -> {
            // 5 日均线
            avgLineFluxSink.next(AvgLine.builder()
                    .stockCode(dayAvgLine.getStockCode())
                    .date(dayAvgLine.getDate())
                    .current(dayAvgLine.getCurrent())
                    .statistic(5)
                    .avg(dayAvgLine.getAvg5())
                    .build());

            // 10 日均线
            avgLineFluxSink.next(AvgLine.builder()
                    .stockCode(dayAvgLine.getStockCode())
                    .date(dayAvgLine.getDate())
                    .current(dayAvgLine.getCurrent())
                    .statistic(10)
                    .avg(dayAvgLine.getAvg10())
                    .build());

            // 20 日均线
            avgLineFluxSink.next(AvgLine.builder()
                    .stockCode(dayAvgLine.getStockCode())
                    .date(dayAvgLine.getDate())
                    .current(dayAvgLine.getCurrent())
                    .statistic(20)
                    .avg(dayAvgLine.getAvg20())
                    .build());
            // 30 日均线
            avgLineFluxSink.next(AvgLine.builder()
                    .stockCode(dayAvgLine.getStockCode())
                    .date(dayAvgLine.getDate())
                    .current(dayAvgLine.getCurrent())
                    .statistic(30)
                    .avg(dayAvgLine.getAvg30())
                    .build());
            avgLineFluxSink.complete();
        });
    }
}
