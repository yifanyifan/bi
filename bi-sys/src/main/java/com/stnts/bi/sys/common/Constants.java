package com.stnts.bi.sys.common;

public interface Constants {

	String LIKE_SYMBOL = "%";

	// ==============
	String MSG_PARAM_ROLE_NAME_NOTNULL = "角色名不能为空";
	String MSG_PARAM_USER_ROLE_NOTNULL = "用户和角色不能为空";
	String MSG_PARAM_USER_ROLE_DUPPRODUCT = "不同角色下产品线不能重复";
	String MSG_PARAM_ROLE_PERM_NOTNULL = "角色和权限列表不能为空";
	String MSG_PARAM_PERM_USER_NOTZERO = "角色存在关联用户,不允许删除";
	String MSG_RETURN_USER_ROLE_DELFAIL = "删除用户权限失败";
	String MSG_MYSQL_DUPLICATE_PK = "不允许重复";
	String MSG_MYSQL_TOOLONG = "字段过长或不符合要求";
	String MSG_PARAM_PRODUCT_DUP = "某角色选择全部产品线，其它角色不可再选产品线";
	// ==============
	//权限关键字  表示拥有所有产品线权限
	String KEY_PERM_ALL_PRODUCT = "-9";

	int LEVEL_TYPE_ROOT = 2;

	String ROOT_ID = "-1";


	String KEY_DEFAULT = "D";
	String KEY_MENU = "M";
	String KEY_PAGE = "P";

	String DSSP_SUCCESS = "success";

	String SHOW_SPLIT = ", ";

}
