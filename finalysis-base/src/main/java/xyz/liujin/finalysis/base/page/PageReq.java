package xyz.liujin.finalysis.base.page;

import io.swagger.annotations.ApiModelProperty;

/**
 * 分页请求对象
 */
public class PageReq {
    public static final int DEFAULT_PAGE_NO = 1;
    public static final int DEFAULT_PAGE_SIZE = 1000;

    @ApiModelProperty("当前页面")
    private Integer pageNo = DEFAULT_PAGE_NO;
    @ApiModelProperty("每页大小")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    @ApiModelProperty("返回数量限制")
    private Integer limit;
    @ApiModelProperty("偏移量")
    private Integer offset;

    public Integer getPageNo() {
        return pageNo;
    }

    public void setPageNo(Integer pageNo) {
        this.pageNo = pageNo;
    }

    public Integer getPageSize() {
        return pageSize;
    }

    public void setPageSize(Integer pageSize) {
        this.pageSize = pageSize;
    }

    public Integer getLimit() {
        return limit;
    }

    public void setLimit(Integer limit) {
        this.limit = limit;
    }

    public Integer getOffset() {
        return offset;
    }

    public void setOffset(Integer offset) {
        this.offset = offset;
    }
}
