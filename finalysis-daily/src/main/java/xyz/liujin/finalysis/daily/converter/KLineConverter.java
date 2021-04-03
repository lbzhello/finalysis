package xyz.liujin.finalysis.daily.converter;

import cn.hutool.core.text.CharSequenceUtil;
import xyz.liujin.finalysis.base.util.DateUtils;
import xyz.liujin.finalysis.daily.dto.KLineDto;
import xyz.liujin.finalysis.daily.entity.KLine;

import java.math.BigDecimal;
import java.util.Optional;

public class KLineConverter {
    public static final KLineDto toKLineDto(KLine kLine) {
        return KLineDto.builder()
                .id(kLine.getId())
                .stockCode(kLine.getStockCode())
                .date(DateUtils.formatDate(kLine.getDate()))
                .open(formatBigDecimal(kLine.getOpen()))
                .close(formatBigDecimal(kLine.getClose()))
                .high(formatBigDecimal(kLine.getHigh()))
                .low(formatBigDecimal(kLine.getLow()))
                .change(formatBigDecimal(kLine.getChange()))
                .pctChange(formatBigDecimal(kLine.getPctChange()))
                .volume(kLine.getVolume() == null ? "0" : kLine.getVolume().toString())
                .amount(formatBigDecimal(kLine.getAmount()))
                .volumeRatio(formatBigDecimal(kLine.getVolumeRatio()))
                .turn(formatBigDecimal(kLine.getTurn()))
                .committee(formatBigDecimal(kLine.getCommittee()))
                .selling(formatBigDecimal(kLine.getSelling()))
                .buying(formatBigDecimal(kLine.getBuying()))
                .build();
    }

    public static final KLine toKLine(KLineDto kLineDto) {
        return KLine.builder()
                .id(kLineDto.getId())
                .stockCode(kLineDto.getStockCode())
                .date(DateUtils.parseDate(kLineDto.getDate()))
                .open(toBigDecimal(kLineDto.getOpen()))
                .close(toBigDecimal(kLineDto.getClose()))
                .high(toBigDecimal(kLineDto.getHigh()))
                .low(toBigDecimal(kLineDto.getLow()))
                .change(toBigDecimal(kLineDto.getChange()))
                .pctChange(toBigDecimal(kLineDto.getPctChange()))
                .volume(Long.valueOf(kLineDto.getVolume()))
                .amount(toBigDecimal(kLineDto.getAmount()))
                .volumeRatio(toBigDecimal(kLineDto.getVolumeRatio()))
                .turn(toBigDecimal(kLineDto.getTurn()))
                .committee(toBigDecimal(kLineDto.getCommittee()))
                .selling(toBigDecimal(kLineDto.getSelling()))
                .buying(toBigDecimal(kLineDto.getBuying()))
                .build();
    }

    private static BigDecimal toBigDecimal(String val) {
        if (CharSequenceUtil.isBlank(val)) {
            return BigDecimal.ZERO;
        }

        return new BigDecimal(val);
    }

    private static String formatBigDecimal(BigDecimal decimal) {
        return Optional.ofNullable(decimal)
                .map(BigDecimal::toString)
                .orElse("0.00");
    }
}
