package com.stnts.bi.entity.common;

import java.util.List;

import com.baomidou.mybatisplus.core.metadata.IPage;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

/**
 * @author liang.zhang
 * @param <T>
 * @date 2020年4月8日
 * @desc
 */
@Data
@ApiModel("分页属性")
public class PageEntity<T> {

	@ApiModelProperty("总记录数")
	private Long total;
	@ApiModelProperty("每页记录数")
	private Long size;
	@ApiModelProperty(value = "页大小")
	private Long index;
	@ApiModelProperty("记录详情")
	private List<T> records;

	public PageEntity(IPage<T> page) {
		this.total = page.getTotal();
		this.records = page.getRecords();
		this.index = page.getCurrent();
		this.size = page.getSize();
	}

	public PageEntity(IPage<?> page, List<T> records) {
		this.total = page.getTotal();
		this.records = records;
		this.index = page.getCurrent();
		this.size = page.getSize();
	}
}
