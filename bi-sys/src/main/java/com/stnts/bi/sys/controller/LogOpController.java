package com.stnts.bi.sys.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.LogOpEntity;
import com.stnts.bi.sys.service.LogOpService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(value = "日志管理", tags = { "日志管理" })
@RestController
@RequestMapping("log/op")
public class LogOpController {

	@Autowired
	private LogOpService logOpService;

	@ApiOperation("日志列表")
	@ApiImplicitParams({ @ApiImplicitParam(name = "page", dataType = "int", value = "页数"),
			@ApiImplicitParam(name = "cnname", dataType = "string", value = "操作人"),
			@ApiImplicitParam(name = "beginDate", dataType = "string", value = "起始时间"),
			@ApiImplicitParam(name = "endDate", dataType = "string", value = "结束时间"),
			@ApiImplicitParam(name = "cond", dataType = "string", value = "模糊条件") })
	@RequestMapping(value = { "list" }, method = { RequestMethod.GET })
	public ResultEntity<Page<LogOpEntity>> listLogOp(@RequestParam(name = "page", required = false) Integer page,
			@RequestParam(name = "cnname", required = false) String cnname,
			@RequestParam(name = "beginDate", required = false) String beginDate,
			@RequestParam(name = "endDate", required = false) String endDate,
			@RequestParam(name = "cond", required = false) String cond) {
		
		return this.logOpService.findLogListByCond(page, cnname, beginDate, endDate, cond);
	}
}
