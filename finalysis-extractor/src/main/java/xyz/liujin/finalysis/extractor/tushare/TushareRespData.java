package xyz.liujin.finalysis.extractor.tushare;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * tushare 响应 data 数据模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TushareRespData {
    /**
     * 字段
     */
    private List<String> fields;
    /**
     * 数据内容
     */
    private List<List<String>> items;
    /**
     * 是否还有更多数据
     */
    private Boolean has_more;
}
