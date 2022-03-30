package com.stnts.bi.sys.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.KpiDescEntity;
import com.stnts.bi.mapper.sys.KpiDescMapper;
import com.stnts.bi.sys.common.Constants;
import com.stnts.bi.sys.common.SysConfig;
import com.stnts.bi.sys.service.KpiDescService;
import com.stnts.bi.sys.utils.SysUtil;
import com.stnts.bi.sys.vos.KpiItemVO;

import lombok.extern.slf4j.Slf4j;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.stereotype.Service;

/**
 * @author liang.zhang
 * @date 2020年3月26日
 * @desc TODO
 */
@Service
@Slf4j
public class KpiDescServiceImpl implements KpiDescService {

	@Autowired
	private SysConfig sysConfig;

	@Autowired
	private KpiDescMapper kpiDescMapper;

	@Override
	public ResultEntity<Page<KpiDescEntity>> findKpiListByName(Integer pageNo, String name) {
		List<KpiDescEntity> kpis = new ArrayList<>();
		try {
			Page<KpiDescEntity> page = SysUtil.toPage(pageNo, this.sysConfig.getPageSize().intValue());
			String _name = SysUtil.toLike(name);
			kpis = this.kpiDescMapper.findKpiListByName(page, _name);
			page.setRecords(kpis);
			return ResultEntity.success(page);
		} catch (Exception e) {
			log.warn("findKpiListByName failed, msg: " + e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
	}

	@Override
	public ResultEntity<String> insertOne(KpiDescEntity kpiDesc) {
		int result = 0;
		try {
			result = this.kpiDescMapper.insert(kpiDesc);
		} catch (Exception e) {
			e.printStackTrace();
			if (e instanceof DuplicateKeyException)
				return ResultEntity.exception(Constants.MSG_MYSQL_DUPLICATE_PK);
			if (e instanceof DataIntegrityViolationException)
				return ResultEntity.exception(Constants.MSG_MYSQL_TOOLONG);
			log.warn("insertOne failed, msg: " + e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
		return (result > 0) ? ResultEntity.success(null) : ResultEntity.failure(null);
	}

	@Override
	public ResultEntity<String> update(KpiDescEntity kpiDesc) {
		int result = 0;
		try {
			result = this.kpiDescMapper.updateById(kpiDesc);
		} catch (Exception e) {
			if (e instanceof DuplicateKeyException)
				return ResultEntity.exception(Constants.MSG_MYSQL_DUPLICATE_PK);
			if (e instanceof DataIntegrityViolationException)
				return ResultEntity.exception(Constants.MSG_MYSQL_TOOLONG);
			log.warn("update failed, msg: " + e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
		return (result > 0) ? ResultEntity.success(null) : ResultEntity.failure(null);
	}

	@Override
	public ResultEntity<String> del(Integer id) {
		int result = 0;
		try {
			result = this.kpiDescMapper.deleteById(id);
		} catch (Exception e) {
			log.warn("del failed, msg: " + e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
		return (result > 0) ? ResultEntity.success(null) : ResultEntity.failure(null);
	}

	@Override
	public ResultEntity<KpiDescEntity> detail(Integer id) {
		KpiDescEntity kpiDesc = null;
		try {
			kpiDesc = this.kpiDescMapper.findKpiById(id);
		} catch (Exception e) {
			log.warn("detail failed, msg: " + e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
		return ResultEntity.success(kpiDesc);
	}

	@Override
	public ResultEntity<List<KpiItemVO>> all() {

		List<KpiItemVO> vos = new ArrayList<KpiItemVO>();
		try {
			List<KpiDescEntity> kpis = kpiDescMapper.all();
			kpis.forEach(kpi -> {
				vos.add(new KpiItemVO(kpi.getKpiKey(), kpi.getKpiDesc()));
			});
		} catch (Exception e) {
			log.warn("all failed, msg: " + e.getMessage());
			return ResultEntity.exception(e.getMessage());
		}
		return ResultEntity.success(vos);
	}
}
