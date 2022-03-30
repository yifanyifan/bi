package com.stnts.tc.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

/**
 * @author liang.zhang
 * @date 2019年11月18日
 * @desc TODO
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class KpiSearch {

	private String rowkey;
	private Integer begin;
	private Integer end;
}
