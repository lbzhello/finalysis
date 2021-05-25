package xyz.liujin.finalysis.base.page;

import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 分页请求对象
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PageQo {
    public static final int DEFAULT_PAGE_NO = 1;
    public static final int DEFAULT_PAGE_SIZE = 1000;

    @ApiModelProperty(value = "当前页面", example = "1")
    @Builder.Default
    private Integer pageNo = DEFAULT_PAGE_NO;
    @ApiModelProperty(value = "每页大小", example = "1000")
    @Builder.Default
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    @ApiModelProperty("总条目数")
    private Integer total;
    @ApiModelProperty("总页数")
    private Integer totalPage;

    @ApiModelProperty(value = "返回数量限制", example = "1000")
    private Integer limit;
    @ApiModelProperty(value = "偏移量", example = "0")
    private Integer offset;

    @ApiModelProperty(value = "order by 从句", hidden = true)
    private String orderBy;
}
