package xyz.liujin.finalysis.base.qo;

import io.swagger.annotations.ApiModelProperty;

public class PageQo {
    public static final int DEFAULT_PAGE_NO = 1;
    public static final int DEFAULT_PAGE_SIZE = 1000;

    @ApiModelProperty("当前页面")
    private Integer pageNo = DEFAULT_PAGE_NO;
    @ApiModelProperty("每页大小")
    private Integer pageSize = DEFAULT_PAGE_SIZE;

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
}
