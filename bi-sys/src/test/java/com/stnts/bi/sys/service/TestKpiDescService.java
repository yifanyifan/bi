package com.stnts.bi.sys.service;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import com.alibaba.fastjson.JSON;
import com.stnts.bi.common.ResultEntity;
import com.stnts.bi.entity.sys.KpiDescEntity;

@RunWith(SpringRunner.class)
@SpringBootTest
public class TestKpiDescService {
	
	@Autowired
	private KpiDescService kpiDescService;

	@Test
	public void  testInsertOne() {
		
		KpiDescEntity kpi = new KpiDescEntity(null ,"zhangl", "zhangl", "zhangl", "zhangl", 11, null);
		
		ResultEntity<String> result = kpiDescService.insertOne(kpi);
		System.out.println(JSON.toJSONString(result));
		Assert.assertTrue(result.getCode().intValue() == 20000);
	}
}
