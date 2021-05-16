package xyz.liujin.finalysis.base.page;

import io.swagger.annotations.ApiModelProperty;

public class PageResp extends PageReq {
    @ApiModelProperty("总条目数")
    private Integer total;
    @ApiModelProperty("总页数")
    private Integer totalPage;

    public Integer getTotal() {
        return total;
    }

    public void setTotal(Integer total) {
        this.total = total;
    }

    public Integer getTotalPage() {
        return totalPage;
    }

    public void setTotalPage(Integer totalPage) {
        this.totalPage = totalPage;
    }
}
