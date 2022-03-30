package com.stnts.bi.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.BiLog;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.KpiDescEntity;
import com.stnts.bi.enums.LogOpTypeEnum;
import com.stnts.bi.sys.service.KpiDescService;
import com.stnts.bi.sys.vos.KpiItemVO;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "指标管理", tags = { "指标管理" })
@RestController
@RequestMapping("kpi")
public class KpiController {

	@Autowired
	private KpiDescService kpiDescService;

	@BiLog
	@ApiOperation("指标列表")
	@ApiImplicitParams({ 
		@ApiImplicitParam(name = "page", required = false, dataType = "int", value = "页数"),
		@ApiImplicitParam(name = "name", required = false, dataType = "string", value = "指标名称") })
	@RequestMapping(value = { "list" }, method = { RequestMethod.GET })
	public ResultEntity<Page<KpiDescEntity>> listKpi(@RequestParam(required = false, defaultValue = "1") Integer page,
			@RequestParam(required = false) String name) {
		return this.kpiDescService.findKpiListByName(page, name);
	}

	@BiLog(LogOpTypeEnum.NEW)
	@ApiOperation("新增指标")
	@RequestMapping(value = { "new" }, method = { RequestMethod.POST })
	public ResultEntity<String> insertOne(@RequestBody KpiDescEntity kpiDesc) {
		return this.kpiDescService.insertOne(kpiDesc);
	}

	@BiLog(LogOpTypeEnum.MOD)
	@ApiOperation("修改指标")
	@RequestMapping(value = { "mod" }, method = { RequestMethod.PUT })
	public ResultEntity<String> update(@RequestBody KpiDescEntity kpiDesc) {
		return this.kpiDescService.update(kpiDesc);
	}

	@BiLog(LogOpTypeEnum.DEL)
	@ApiOperation("删除指标")
	@ApiImplicitParam(name = "id", paramType = "path", dataType = "int", value = "指标ID")
	@RequestMapping(value = { "{id}" }, method = { RequestMethod.DELETE })
	public ResultEntity<String> delete(@PathVariable(required = true, name = "id") Integer id) {
		return this.kpiDescService.del(id);
	}

	@BiLog
	@ApiOperation("指标详情")
	@ApiImplicitParam(name = "id", paramType = "path", dataType = "int", value = "指标ID")
	@RequestMapping(value = { "{id}" }, method = { RequestMethod.GET })
	public ResultEntity<KpiDescEntity> detail(@PathVariable(required = true, name = "id") Integer id) {
		return this.kpiDescService.detail(id);
	}
	
	@ApiOperation("指标字典")
	@GetMapping("all")
	public ResultEntity<List<KpiItemVO>> all(){
		return kpiDescService.all();
	}
}
