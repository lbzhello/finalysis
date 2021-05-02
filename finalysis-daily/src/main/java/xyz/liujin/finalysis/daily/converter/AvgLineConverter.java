package xyz.liujin.finalysis.daily.converter;

import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.daily.entity.AvgLine;
import xyz.liujin.finalysis.daily.entity.VAvgLine;

/**
 * 均线视图转换
 * 转换成具体的数据库
 */
public class AvgLineConverter {
    public static Flux<AvgLine> toAvgLine(VAvgLine vAvgLine) {
        return Flux.create(avgLineFluxSink -> {
            // 5 日均线
            avgLineFluxSink.next(AvgLine.builder()
                    .stockCode(vAvgLine.getStockCode())
                    .date(vAvgLine.getDate())
                    .current(vAvgLine.getCurrent())
                    .statistic(5)
                    .avg(vAvgLine.getAvg5())
                    .build());

            // 10 日均线
            avgLineFluxSink.next(AvgLine.builder()
                    .stockCode(vAvgLine.getStockCode())
                    .date(vAvgLine.getDate())
                    .current(vAvgLine.getCurrent())
                    .statistic(10)
                    .avg(vAvgLine.getAvg10())
                    .build());

            // 20 日均线
            avgLineFluxSink.next(AvgLine.builder()
                    .stockCode(vAvgLine.getStockCode())
                    .date(vAvgLine.getDate())
                    .current(vAvgLine.getCurrent())
                    .statistic(20)
                    .avg(vAvgLine.getAvg20())
                    .build());
            // 30 日均线
            avgLineFluxSink.next(AvgLine.builder()
                    .stockCode(vAvgLine.getStockCode())
                    .date(vAvgLine.getDate())
                    .current(vAvgLine.getCurrent())
                    .statistic(30)
                    .avg(vAvgLine.getAvg30())
                    .build());
            avgLineFluxSink.complete();
        });
    }
}
