package xyz.liujin.finalysis.extractor;

import org.springframework.lang.Nullable;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.daily.dto.KLineDto;

import java.time.LocalDate;
import java.util.List;

public interface KLineExtractor {
    /**
     * 爬取日 K 线数据
     * @param codes     股票代码列表，为空则表示所有，例如 [000001, 000002]
     * @param startDate yyyy-MM-dd 开始时间，包含，为空则不过滤; 例如 2021-01-01
     * @param endDate   yyyy-MM-dd 结束时间，包含，为空则不过滤; 例如 2021-01-02
     * @return
     */
    Flux<KLineDto> extractKLine(@Nullable LocalDate startDate, @Nullable LocalDate endDate, @Nullable List<String> codes);
}
