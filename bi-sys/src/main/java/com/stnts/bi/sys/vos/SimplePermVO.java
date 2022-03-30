package com.stnts.bi.sys.vos;

import java.util.List;

import lombok.Data;

/**
 * @author liang.zhang
 * @date 2020年5月20日
 * @desc TODO
 */
@Data
public class SimplePermVO {

	private Integer id;
	private String name;
	private String code;
	private List<SimplePermVO> perms;
}
