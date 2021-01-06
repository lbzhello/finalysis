package xyz.liujin.finalysis.spider.tushare;

import cn.hutool.json.JSONUtil;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import okhttp3.Response;
import reactor.core.publisher.Flux;
import xyz.liujin.finalysis.spider.util.HttpUtils;

import java.util.Map;

/**
 * tushare http api 请求参数
 * 官网：https://tushare.pro/
 *
 * 说明：
 * N 表示非必填
 * Y 表示必填
 * 默认  Y 必填
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TushareReq {
    /**
     * tushare token；登录后从用户中心获取
     */
    public static final String TOKEN = "2ea612d41ee1fdb2fed04bb097debcbcee8d43eeba5570a2605d6914";

    /**
     * API 调用接口
     */
    public static final String API = "http://api.waditu.com";

    /**
     * 接口名称，比如stock_basic
     */
    private String api_name;
    /**
     * 用户唯一标识，可通过登录pro网站获取
     */
    private String token;
    /**
     * 接口参数，如daily接口中start_date和end_date
     */
    private Map<String, Object> params;
    /**
     * 字段列表，用于接口获取指定的字段，以逗号分隔，如"open,high,low,close"
     * N 默认全部字段
     */
    private String fields;

    public Flux<Response> req() {
        return HttpUtils.postJSON(API)
                .body(JSONUtil.toJsonStr(this))
                .req();
    }

    /**
     * params 参数模型抽象
     */
    public interface Params {
        /**
         * 获取接口名字
         * @return
         */
        String getApiName();

        /**
         * 根据接口发送请求
         * @return
         */
        default Flux<Response> req() {
            return TushareReq.builder()
                    .token(TOKEN)
                    .api_name(getApiName())
                    .params(JSONUtil.parseObj(this))
//                .fields("")
                    .build()
                    .req();
        }
    }

    /**
     * 获取日线数据
     * https://tushare.pro/document/2?doc_id=27
     */
    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static final class Daily implements Params {
        /**
         * 股票代码（支持多个股票同时提取，逗号分隔）
         * N: 默认全部股票
         */
        private String ts_code;

        /**
         * 交易日期（YYYYMMDD）
         */
        private String trade_date;

        /**
         * 开始日期(YYYYMMDD)
         */
        private String start_date;

        /**
         * 结束日期(YYYYMMDD)
         */
        private String end_date;

        @Override
        public String getApiName() {
            return "daily";
        }
    }

}
