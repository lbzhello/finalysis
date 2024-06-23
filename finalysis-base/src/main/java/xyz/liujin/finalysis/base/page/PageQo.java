package xyz.liujin.finalysis.base.page;

import io.swagger.v3.oas.annotations.media.Schema;
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

    @Schema(description = "当前页面", example = "1")
    @Builder.Default
    private Integer pageNo = DEFAULT_PAGE_NO;
    @Schema(description = "每页大小", example = "1000")
    @Builder.Default
    private Integer pageSize = DEFAULT_PAGE_SIZE;

    @Schema(description = "总条目数", hidden = true)
    private Integer total;
    @Schema(description = "总页数", hidden = true)
    private Integer totalPage;

    @Schema(description = "返回数量限制", example = "1000")
    private Integer limit;
    @Schema(description = "偏移量", example = "0")
    private Integer offset;

    @Schema(description = "order by 从句", hidden = true)
    private String orderBy;
}
