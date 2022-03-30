package com.stnts.tc.common;

/**
 * @author liang.zhang
 * @date 2019年11月18日
 * @desc TODO
 */
public interface Constants {
	
	int DEFAULT = -1;
	int BEGIN_DEFAULT = 1;
	int END_DEFAULT_M = 12;
	
	String RESULT_CARD = "CARD";
	String RESULT_CARD_6M = "CARD_6M";
	String RESULT_NEWLY = "NEWLY";
	String KEY_SPLIT = "_";
	String KEY_DATE_SPLIT = "-";
	
	String KEY_SRC = "SRC";
	String KEY_DEST = "DEST";
	String KEY_COMP = "COMP";
	String KEY_OPTIONS = "options";
	String KEY_IS_SHOW_COMP = "isShowComp";
	String KEY_TABLE_OPTIONS = "table";
	String KEY_PLUGINS = "plugins";
	
	String DE_F = "info";
	String DE_C = "info";
	
	String VTYPE_INT = "int";
	String VTYPE_FLOAT = "float";
	
	//网吧索引表
	String B_DE_BA_F = "info";
	String B_DE_BA_C = "info";
//	String B_DE_BA_T = "tc:bar";
	//插件索引表
	String P_DE_BA_F = "info";
	String P_DE_BA_C = "info";
//	String P_DE_BA_T = "tc:bar";
	
	String TABLE_KPI = "tc:index_test";
	String TABLE_KPI_FAMILY = "kpi";
	
	String KEY_GID = "gid";
	
	String REDIS_BAR_HASH = "BI_HOT_BARS_HASH";
	
}
