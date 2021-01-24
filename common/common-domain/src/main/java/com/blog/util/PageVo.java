package com.blog.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

@Data
@ApiModel(description = "分页响应实体")
public class PageVo<T> implements Serializable {
    private static final long serialVersionUID = -5668554152671000202L;
    @ApiModelProperty(value = "当前页数", name = "page")
    private Integer page = 1;

    @ApiModelProperty(value = "每页条数", name = "size")
    private Integer size = 10;

    @ApiModelProperty(value = "总条数", name = "total")
    private Long total;

    @ApiModelProperty(value = "数据", name = "datas")
    private List<T> items;

    private boolean hasNextPage;

    /**
     * @param datas 数据
     * @param page  当前页数
     * @param size  每页条数
     * @param total 总数
     * @author kongchong
     * date: 2019/5/16 14:10
     */
    public static <T> PageVo<T> convertResultsWithPageInfo(List<T> datas, Integer page, Integer size, Long total) {
        PageVo<T> pageVo = new PageVo<>();
        pageVo.setPage(page);
        pageVo.setSize(size);
        pageVo.setTotal(total);
        pageVo.setItems(datas);
        return pageVo;
    }
}
