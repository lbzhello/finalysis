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

    @ApiModelProperty("当前页面")
    @Builder.Default
    private Integer pageNo = DEFAULT_PAGE_NO;
    @ApiModelProperty("每页大小")
    @Builder.Default
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    @ApiModelProperty("总条目数")
    private Integer total;
    @ApiModelProperty("总页数")
    private Integer totalPage;

    @ApiModelProperty("返回数量限制")
    private Integer limit;
    @ApiModelProperty("偏移量")
    private Integer offset;

    @ApiModelProperty("order by 从句")
    private String orderBy;
}
