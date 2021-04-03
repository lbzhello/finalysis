package xyz.liujin.finalysis.extractor.tushare.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 通用 api 接口响应模型
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TushareResp {
    // 响应 csv 字段 fields 在 json 中的位置
    public static final String FIELDS_PATH = "/data/fields";
    // 响应 csv 记录 items 在 json 的位置
    public static final String ITEMS_PATH = "/data/items";
    // 接口返回码，2002表示权限问题
    private Integer code;
    // 错误信息，比如“系统内部错误”，“没有权限”等
    private String msg;
    // 数据，data里包含fields和items字段，分别为字段和数据内容
    private TushareRespData data;
}
