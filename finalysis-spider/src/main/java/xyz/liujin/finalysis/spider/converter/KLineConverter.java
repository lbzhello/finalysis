package xyz.liujin.finalysis.spider.converter;

import cn.hutool.core.text.CharSequenceUtil;
import xyz.liujin.finalysis.common.util.DateUtils;
import xyz.liujin.finalysis.spider.dto.KLineDto;
import xyz.liujin.finalysis.spider.entity.KLine;

import java.math.BigDecimal;

public class KLineConverter {
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
}
