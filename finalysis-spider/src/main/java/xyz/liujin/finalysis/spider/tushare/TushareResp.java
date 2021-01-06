package xyz.liujin.finalysis.spider.tushare;

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
    // 接口返回码，2002表示权限问题
    private Integer code;
    // 错误信息，比如“系统内部错误”，“没有权限”等
    private String msg;
    // 数据，data里包含fields和items字段，分别为字段和数据内容
    private TushareRespData data;
}
