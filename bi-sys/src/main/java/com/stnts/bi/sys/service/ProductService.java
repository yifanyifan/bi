package com.stnts.bi.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.ProductEntity;

/**
 * @author liang.zhang
 * @date 2020年6月22日
 * @desc TODO
 */
public interface ProductService {
	
	ResultEntity<List<ProductEntity>> all(String name);

	/**
	 * 同步产品线
	 * @return
	 */
	public ResultEntity<String> syncProduct();

	/**
	 * 产品线列表[分页]
	 * @param name
	 * @return
	 */
    ResultEntity<Page<ProductEntity>> list(Integer pageNo, String name);

	/**
	 * 删除产品线下用户
	 * @param productId
	 */
	ResultEntity<String> delUsers(Integer productId, List<Integer> userIds);

	/**
	 * 产品线绑定数据层级
	 * @param productBindVO
	 * @return
	 */
	ResultEntity<String> bindDataLevel(ProductEntity productEntity);
}
