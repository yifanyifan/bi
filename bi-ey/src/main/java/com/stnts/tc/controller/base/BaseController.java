package com.stnts.tc.controller.base;

import java.util.List;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stnts.bi.sql.vo.QueryChartResultVO;
import com.stnts.tc.common.ResultEntity;
import com.stnts.tc.service.BaseService;
import com.stnts.tc.vo.IndexVO;

/**
 * @author liang.zhang
 * @date 2019年12月10日
 * @desc TODO
 * 基础服务controller
 */
@RestController
@RequestMapping("base")
public class BaseController {
	
	@Autowired
	private BaseService baseService;
	
	
	@RequestMapping("index/bar")
	public ResultEntity<Object> barList(@RequestParam(required=false) String key){
		
		List<IndexVO> result = baseService.listBar(key);
		return ResultEntity.success(result);
	}
	
	@RequestMapping("index/plugin")
	public ResultEntity<Object> pluginList(@RequestParam(required=false) String key){
		
		List<IndexVO> result = baseService.listPlugin(key);
		return ResultEntity.success(result);
	}
	
	@PostMapping("chart")
	public ResultEntity<QueryChartResultVO> getChart(@RequestParam("data") String data) {
        return baseService.getChart(data);
    }
	
	@RequestMapping("export")
    public void exportChart(@RequestParam("data") String data, HttpServletResponse response) {
        baseService.export(data, response);
    }
}
