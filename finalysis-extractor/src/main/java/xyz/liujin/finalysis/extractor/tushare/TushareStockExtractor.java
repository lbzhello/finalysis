package xyz.liujin.finalysis.extractor.tushare;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.ResourceUtils;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.base.constant.StockBoardEnum;
import xyz.liujin.finalysis.base.constant.StockConst;
import xyz.liujin.finalysis.base.json.CsvMapper;
import xyz.liujin.finalysis.base.util.DateUtils;
import xyz.liujin.finalysis.extractor.StockExtractor;
import xyz.liujin.finalysis.extractor.tushare.api.Tushare;
import xyz.liujin.finalysis.extractor.tushare.api.TushareResp;
import xyz.liujin.finalysis.extractor.tushare.dto.TushareStock;
import xyz.liujin.finalysis.extractor.tushare.util.TushareUtil;
import xyz.liujin.finalysis.stock.entity.Stock;

import java.io.File;
import java.time.LocalDate;
import java.util.Objects;

@Component
public class TushareStockExtractor implements StockExtractor {
    private static Logger logger = LoggerFactory.getLogger(TushareStockExtractor.class);

    @Override
    public Flux<Stock> extractStock() {
        return Tushare.StockBasic.builder()
                .build()
                .reqBody("symbol,name,list_status,list_date")
                .flatMap(bodyStr -> {
                    try {
                        // 获取映射文件
                        File file = ResourceUtils.getFile("classpath:tushare/stock_basic_2_stock.yml");
                        return CsvMapper.yamlFile(TushareResp.FIELDS_PATH, TushareResp.ITEMS_PATH, file)
                                .eval(bodyStr, TushareStock.class)
                                .map(this::toStock);
                    } catch (Exception e) {
                        logger.error("extract stock failed", e);
                    }
                    return Flux.just();
                });
    }

    private Stock toStock(TushareStock tushareStock) {
        // 股票状态
        String statStr = tushareStock.getStat();
        int stat = 0;
        if (Objects.equals(statStr, "L")) {
            stat = StockConst.NORMAL;
        } else if (Objects.equals(statStr, "D")) {
            stat = StockConst.DE_LISTING;
        } else if (Objects.equals(statStr, "P")) {
            stat = StockConst.PAUSE_LISTING;
        }
        String dateStr = tushareStock.getListingDate();
        LocalDate offsetDate = DateUtils.parseDate(dateStr, "yyyyMMdd");

        return Stock.builder()
                .stockCode(TushareUtil.parseCode(tushareStock.getStockCode()))
                .stockName(tushareStock.getStockName())
                .board(StockBoardEnum.getBoardByCode(tushareStock.getStockCode()))
                .stat(stat)
                .listingDate(offsetDate)
                .build();
    }
}
