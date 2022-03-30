package com.stnts.bi.plugin.controller;

import java.io.IOException;

import javax.servlet.http.HttpServletResponse;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.plugin.service.BaseService;
import com.stnts.bi.sql.vo.QueryChartResultVO;

/**
 * @author liang.zhang
 * @date 2020年8月5日
 * @desc TODO
 *   查维表用
 */
@RestController
@RequestMapping("common")
public class CommonController {

	@Autowired
	private BaseService baseService;

	@PostMapping("chart")
	public ResultEntity<QueryChartResultVO> chart(@RequestParam("data") String data) {
		return baseService.getChart(data);
	}
	
	@RequestMapping("export")
    public void exportChart(@RequestParam("data") String data, HttpServletResponse response) throws IOException {
        baseService.export(data, response);
    }
}
