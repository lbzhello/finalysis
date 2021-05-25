package xyz.liujin.finalysis.analysis.constant;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.Objects;

/**
 * 股票分析指标/选项
 * 用于股票分析时从某个纬度选择股票
 */
public enum  AnalysisOpt {
    HOT_NEWS("HOT_NEWS", "热点消息面"),
    FIVE_CROSS_TEN("FIVE_CROSS_TEN", "5 日线突破 10 日线"),
    FIVE_ABOVE_TEN("FIVE_ABOVE_TEN", "5 日线在 10 日线上方"),
    HEAVY_VOL_RATIO("HEAVY_VOL", "放量"),
    NIL("NIL", "无"),
    ;

    // 选项代码
    private String code;
    // 选项说明
    private String description;

    private static final Logger logger = LoggerFactory.getLogger(AnalysisOpt.class);

    AnalysisOpt(String code, String description) {
        this.code = code;
        this.description = description;
    }

    /**
     * 将 code 解析为分析选项
     * @param code
     * @return
     */
    public static AnalysisOpt parse(String code) {
        return Arrays.stream(values())
                .filter(it -> Objects.equals(code, it.getCode()))
                .findAny()
                .orElse(NIL);
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
