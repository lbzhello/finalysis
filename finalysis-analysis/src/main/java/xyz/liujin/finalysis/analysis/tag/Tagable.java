package xyz.liujin.finalysis.analysis.tag;

import xyz.liujin.finalysis.analysis.entity.TagScore;

/**
 * 标签标识接口，实现此接口标识会生成一个标签
 */
public interface Tagable {
    TagScore getTag();
}
