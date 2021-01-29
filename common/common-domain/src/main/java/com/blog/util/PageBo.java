package com.blog.util;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

@Data
@ApiModel(description = "分页请求实体")
public class PageBo<T> implements Serializable {

	@ApiModelProperty(value="当前页数", required = true, example = "1")
	private Integer page;

	@ApiModelProperty(value="每页显示数", required = true, example = "10")
	private Integer size;

	@ApiModelProperty(value="是否有下一页")
	private boolean hasNextPage;

	@ApiModelProperty(value="参数")
	private T param;

	public static <K> PageBo<K> init(PageBo<K> pageBo){
		if(pageBo == null){
			pageBo = new PageBo<>();
		}
		if(pageBo.getPage() == null || pageBo.getPage() < 1){
			pageBo.setPage(1);
		}
		if(pageBo.getSize() == null || pageBo.getSize() < 0){
			pageBo.setSize(10);
		}
		return pageBo;
	}
}
