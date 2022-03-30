package com.stnts.bi.sys.service;

import java.util.List;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.KpiDescEntity;
import com.stnts.bi.sys.vos.KpiItemVO;

/**
 * @author liang.zhang
 * @date 2020年3月26日
 * @desc TODO
 */
public interface KpiDescService {

	/**
	 * 列表查询
	 * @param pageNo
	 * @param name
	 * @return
	 */
	ResultEntity<Page<KpiDescEntity>> findKpiListByName(Integer pageNo, String name);
	
	/**
	 * 插入数据
	 * @param kpiDesc
	 * @return
	 */
	ResultEntity<String> insertOne(KpiDescEntity kpiDesc);
	
	/**
	 * 更新
	 * @param kpiDesc
	 * @return
	 */
	ResultEntity<String> update(KpiDescEntity kpiDesc);

	/**
	 * 删除
	 * @param id
	 * @return
	 */
	ResultEntity<String> del(Integer id);

	/**
	 * 指标详情
	 * @param id
	 * @return
	 */
	ResultEntity<KpiDescEntity> detail(Integer id);
	
	/**
	 * 查询所有键值对
	 * @return
	 */
	ResultEntity<List<KpiItemVO>> all();
}
