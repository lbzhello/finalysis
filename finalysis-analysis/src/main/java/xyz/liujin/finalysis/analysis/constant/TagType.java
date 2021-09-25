package xyz.liujin.finalysis.analysis.constant;

/**
 * 标签类型表
 * @author liubaozhu lbzhello@qq.com
 * @since 2021/9/25
 */
public enum TagType {
    // 增幅比指标
    INCREASE_RATIO("increase_ratio"),
    ;
    private String name;

    TagType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
