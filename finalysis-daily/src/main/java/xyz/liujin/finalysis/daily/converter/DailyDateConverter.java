package xyz.liujin.finalysis.daily.converter;

import xyz.liujin.finalysis.daily.dto.DailyData;
import xyz.liujin.finalysis.daily.entity.KLine;

public class DailyDateConverter {
    public static DailyData toDailyData(KLine kLine) {
        return DailyData.builder()
                .stockCode(kLine.getStockCode())
                .stockName(null)
                .open(kLine.getOpen())
                .close(kLine.getClose())
                .high(kLine.getHigh())
                .low(kLine.getLow())
                .change(kLine.getChange())
                .pctChange(kLine.getPctChange())
                .date(kLine.getDate())
                .volume(kLine.getVolume())
                .volumeRatio(kLine.getVolumeRatio())
                .amount(kLine.getAmount())
                .turn(kLine.getTurn())
                .marketValue(null)
                .build();
    }
}
