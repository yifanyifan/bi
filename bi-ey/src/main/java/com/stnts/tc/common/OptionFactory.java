package com.stnts.tc.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.stnts.tc.vo.Option;

/**
 * @author liang.zhang
 * @date 2019年12月12日
 * @desc TODO
 */
public class OptionFactory implements Constants{
	
	public static List<Option> EY_GLOBAL_BASIC_PROFILE(){
		
		List<Option> ops = new ArrayList<Option>();
//		ops.add(new Option("B_VA_V", "管理端验证网吧数", false));
//		ops.add(new Option("B_A_V", "网吧活跃数", false));
//		ops.add(new Option("B_OC_V", "上座率", false));
//		ops.add(new Option("P_BU_V", "PC安装数", false));
//		ops.add(new Option("P_A_V", "PC日活数"));
//		ops.add(new Option("P_BA_A_V", "PC基础日活数", false));
//		ops.add(new Option("P_PR_A_V", "PC产品日活数"));
		
		ops.add(new Option("B_VA_V", "管理端验证网吧数", false, "", "", VTYPE_INT, "家"));
		ops.add(new Option("B_A_V", "活跃网吧数", false, "", "", VTYPE_INT, "家"));
		ops.add(new Option("B_OC_V", "上座率", false, "", "", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("P_BU_V", "安装PC", false, "", "", VTYPE_INT, "台"));
		ops.add(new Option("P_A_V", "日活PC", true, "", "", VTYPE_INT, "台"));
		ops.add(new Option("P_BA_A_V", "基础日活PC", false, "", "", VTYPE_INT, "台"));
		ops.add(new Option("P_PR_A_V", "产品日活PC", true, "", "", VTYPE_INT, "台"));
		return ops;
	}
	
	public static List<Option> EY_GLOBAL_BASIC_CHANNEL(){
		
		List<Option> ops = new ArrayList<Option>();
		ops.add(new Option("C_O_R_ER", "通道开启率EYRUN", true, "通道开启率", "EYRUN", "float", "百分比"));
		ops.add(new Option("C_O_R_TP", "通道开启率TPSP", true, "通道开启率", "TPSP", "float", "百分比"));
		ops.add(new Option("C_O_R_AP", "通道开启率APPCENTER", true, "通道开启率", "APPCENTER", "float", "百分比"));
		ops.add(new Option("C_AL_R_ER", "通道存活率EYRUN", true, "通道存活率", "EYRUN", "float", "百分比"));
		ops.add(new Option("C_AL_R_TP", "通道存活率TPSP", true, "通道存活率", "TPSP", "float", "百分比"));
		ops.add(new Option("C_AL_R_AP", "通道存活率APPCENTER", true, "通道存活率", "APPCENTER", "float", "百分比"));
		ops.add(new Option("Y_REQ_R", "友拓数据请求率", true, "友拓数据", "请求率", "float", "百分比"));
		ops.add(new Option("Y_CPM_R", "友拓数据展示率", true, "友拓数据", "展示率", "float", "百分比"));
		return ops;
	}
	
	public static List<Option> EY_GLOBAL_BASIC_SCORE(){
		
		List<Option> ops = new ArrayList<Option>();
		ops.add(new Option("P_S_AU_V", "合规评分PC评分", true, "合规评分", "PC评分", VTYPE_INT, "台"));
		ops.add(new Option("B_S_AU_V", "合规评分PC评分", true, "合规评分", "网吧评分", VTYPE_INT, "家"));
		ops.add(new Option("P_S_C_V", "通道评分PC评分", true, "通道评分", "PC评分", VTYPE_INT, "台"));
		ops.add(new Option("B_S_C_V", "通道评分网吧评分", true, "通道评分", "网吧评分", VTYPE_INT, "家"));
		return ops;
	}
	
	public static Map<String, List<Option>> EY_GLOBAL_PLUGIN_PROFILE(){
		
		Map<String, List<Option>> opMap = new HashMap<String, List<Option>>();
		
//		List<Option> ops = new ArrayList<Option>();
//		ops.add(new Option("bar_reach", "到达网吧数"));
//		ops.add(new Option("bar_start", "启动网吧数"));
//		ops.add(new Option("pc_reach", "到达PC数"));
//		ops.add(new Option("pc_start", "启动PC数"));
//		ops.add(new Option("pc_rate_fin", "总体生效率"));
//		ops.add(new Option("pc_reach_rate", "到达率"));
//		ops.add(new Option("pc_start_rate", "开启率"));
//		opMap.put("table", ops);
		
		List<Option> opsCard = new ArrayList<>();
//		opsCard.add(new Option("PI_BIZ_B", "", "业务插件", "覆盖网吧数"));
//		opsCard.add(new Option("PI_BIZ_P", "", "业务插件", "覆盖PC数"));
//		opsCard.add(new Option("PI_BIZ", "", "业务插件", "插件数"));
//		opsCard.add(new Option("PI_BA_B", "", "基础插件", "覆盖网吧数"));
//		opsCard.add(new Option("PI_BA_P", "", "基础插件", "覆盖PC数"));
//		opsCard.add(new Option("PI_BA", "", "基础插件", "插件数"));
		
		opsCard.add(new Option("PI_BIZ_B", "业务插件覆盖网吧数", true, "业务插件", "覆盖网吧数", VTYPE_INT, "家"));
		opsCard.add(new Option("PI_BIZ_P", "业务插件覆盖PC数", true, "业务插件", "覆盖PC数", VTYPE_INT, "台"));
		opsCard.add(new Option("PI_BIZ", "业务插件数", true, "业务插件", "插件数", VTYPE_INT, "个"));
		opsCard.add(new Option("PI_BA_B", "基础插件覆盖网吧数", true, "基础插件", "覆盖网吧数", VTYPE_INT, "家"));
		opsCard.add(new Option("PI_BA_P", "基础插件覆盖PC数", true, "基础插件", "覆盖PC数", VTYPE_INT, "台"));
		opsCard.add(new Option("PI_BA", "基础插件数", true, "基础插件", "插件数", VTYPE_INT, "个"));
		opMap.put("card", opsCard);
		return opMap;
	}
	
	public static List<Option> EY_GLOBAL_BAR_NEW(){
		
		List<Option> ops = new ArrayList<Option>();
		ops.add(new Option("B_N_V", "新增网吧数", true, "", "", VTYPE_INT, "家"));
		ops.add(new Option("P_N_V", "新增PC", true, "", "", VTYPE_INT, "台"));
		return ops;
	}
	
	public static Map<String, List<Option>> EY_BAR_PLUGIN_PROFILE(){
		
		Map<String, List<Option>> opMap = new HashMap<String, List<Option>>();
		
//		List<Option> ops = new ArrayList<Option>();
//		ops.add(new Option("bar_reach", "到达网吧数"));
//		ops.add(new Option("bar_start", "启动网吧数"));
//		ops.add(new Option("pc_reach", "到达PC数"));
//		ops.add(new Option("pc_start", "启动PC数"));
//		ops.add(new Option("pc_rate_fin", "总体生效率"));
//		ops.add(new Option("pc_reach_rate", "到达率"));
//		ops.add(new Option("pc_start_rate", "开启率"));
//		opMap.put("table", ops);
		//opsCard.add(new Option("PI_BA", "基础插件数", true, "基础插件", "插件数", VTYPE_INT, "个"));
		List<Option> opsCard = new ArrayList<>();
		opsCard.add(new Option("biz_pc_plug_cover", "业务插件覆盖PC数", true, "业务插件", "覆盖PC数", VTYPE_INT, "台"));
		opsCard.add(new Option("biz_num_plug", "业务插件数", true, "业务插件", "插件数", VTYPE_INT, "个"));
		opsCard.add(new Option("base_pc_plug_cover", "基础插件覆盖PC数", true, "基础插件", "覆盖PC数", VTYPE_INT, "台"));
		opsCard.add(new Option("base_num_plug", "基础插件数", true, "基础插件", "插件数", VTYPE_INT, "个"));
		opMap.put("card", opsCard);
		return opMap;
	}
	
//	public static List<Option> EY_GLOBAL_PLUGIN_ANALYSIS_BAR(){
//		
//		List<Option> ops = new ArrayList<Option>();
//		ops.add(new Option("bar_active", "活跃网吧数"));
//		ops.add(new Option("bar_reach", "到达网吧数数"));
//		ops.add(new Option("bar_start", "启动网吧数"));
//		ops.add(new Option("bar_effect", "配置生效网吧数"));
//		ops.add(new Option("bar_business", "业务生效网吧数"));
//		//
//		return ops;
//	}
//	
//	public static List<Option> EY_GLOBAL_PLUGIN_ANALYSIS_PC(){
//		
//		List<Option> ops = new ArrayList<Option>();
//		ops.add(new Option("bar_active", "活跃网吧数"));
//		ops.add(new Option("bar_reach", "到达网吧数数"));
//		ops.add(new Option("bar_start", "启动网吧数"));
//		ops.add(new Option("bar_effect", "配置生效网吧数"));
//		ops.add(new Option("bar_business", "业务生效网吧数"));
//		//
//		return ops;
//	}
	
	/**
	 * 易游 网吧 基础 指标
	 * @return
	 */
	public static List<Option> EY_BAR_BASIC_KPI(){
		//opsCard.add(new Option("PI_BA", "基础插件数", true, "基础插件", "插件数", VTYPE_INT, "个"));
		List<Option> ops = new ArrayList<Option>();
		ops.add(new Option("pc_start", "总活跃PC", true, "", "", VTYPE_INT, "台"));
		ops.add(new Option("pc_build", "安装PC", true, "", "", VTYPE_INT, "台"));
		ops.add(new Option("szl", "上座率", true, "", "", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("pc_active", "日活PC", true, "", "", VTYPE_INT, "台"));
		ops.add(new Option("pc_base", "基础日活PC", true, "", "", VTYPE_INT, "台"));
		ops.add(new Option("pc_product", "产品日活PC", true, "", "", VTYPE_INT, "台"));
		//
		ops.add(new Option("resource_num", "服务端资源数", true, "", "", VTYPE_INT, "个"));
		ops.add(new Option("resource_down", "服务端下载资源数", true, "", "", VTYPE_INT, "个"));
		ops.add(new Option("resource_boot", "客户机启动资源数", true, "", "", VTYPE_INT, "个"));
		return ops;
	}
	
	public static List<Option> EY_BAR_BASIC_CHANNEL(){
		
		List<Option> ops = new ArrayList<Option>();
		ops.add(new Option("start_rate_eyrun", "通道开启率EYRUN", true, "通道开启率", "EYRUN", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("start_rate_tpsp", "通道开启率TPSP", true, "通道开启率", "TPSP", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("start_rate_center", "通道开启率APPCENTER", true, "通道开启率", "APPCENTER", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("suv_rate_eyrun", "通道存活率EYRUN", true, "通道存活率", "EYRUN", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("suv_rate_tpsp", "通道存活率TPSP", true, "通道存活率", "TPSP", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("suv_rate_center", "通道存活率APPCENTER", true, "通道存活率", "APPCENTER", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("req_rate_youtop", "友拓数据请求率", true, "友拓数据", "请求率", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("pv_rate_youtop", "友拓数据展示率", true, "友拓数据", "展示率", VTYPE_FLOAT, "百分比"));
		
		return ops;
	}
	
	public static Object EY_GLOBAL_PLUGIN_ANALYSIS() {
		
		List<Option> ops = new ArrayList<Option>();
		ops.add(new Option("bar_active", "网吧数活跃网吧数", true, "网吧数", "活跃网吧数", VTYPE_INT, "家"));
		ops.add(new Option("bar_reach", "网吧数到达网吧数", true, "网吧数", "到达网吧数", VTYPE_INT, "家"));
		ops.add(new Option("bar_start", "网吧数启动网吧数", true, "网吧数", "启动网吧数", VTYPE_INT, "家"));
		ops.add(new Option("bar_effect", "网吧数配置生效网吧数", true, "网吧数", "配置生效网吧数", VTYPE_INT, "家"));
		ops.add(new Option("bar_business", "网吧数业务生效网吧数", true, "网吧数", "业务生效网吧数", VTYPE_INT, "家"));
		
		ops.add(new Option("pc_active", "PC数总活跃PC数", true, "PC数", "总活跃PC数", VTYPE_INT, "台"));
		ops.add(new Option("pc_reach", "PC数到达PC数", true, "PC数", "到达PC数", VTYPE_INT, "台"));
		ops.add(new Option("pc_start", "PC数启动PC数", true, "PC数", "启动PC数", VTYPE_INT, "台"));
		ops.add(new Option("pc_effect", "PC数配置生效PC数", true, "PC数", "配置生效PC数", VTYPE_INT, "台"));
		ops.add(new Option("pc_business", "PC数业务生效PC数", true, "PC数", "业务生效PC数", VTYPE_INT, "台"));
		
		ops.add(new Option("pc_rate_fin", "PC数总体生效率", true, "PC数", "总体生效率", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("pc_reach_rate", "PC数到达率", true, "PC数", "到达率", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("pc_start_rate", "PC数启动率", true, "PC数", "启动率", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("pc_effect_rate", "PC数配置生效率", true, "PC数", "配置生效率", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("pc_business_rate", "PC数业务生效率", true, "PC数", "业务生效率", VTYPE_FLOAT, "百分比"));
		
		return ops;
	}
	
	public static Object EY_BAR_PLUGIN_ANALYSIS() {
		
		List<Option> ops = new ArrayList<Option>();
		ops.add(new Option("pc_active", "总活跃PC数", true, "", "", VTYPE_INT, "台"));
		ops.add(new Option("pc_reach", "到达PC数", true, "", "", VTYPE_INT, "台"));
		ops.add(new Option("pc_start", "启动PC数", true, "", "", VTYPE_INT, "台"));
		ops.add(new Option("pc_effect", "配置生效PC数", true, "", "", VTYPE_INT, "台"));
		ops.add(new Option("pc_business", "业务生效PC数", true, "", "", VTYPE_INT, "台"));
		
		ops.add(new Option("pc_reach_rate", "到达率", true, "", "", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("pc_start_rate", "启动率", true, "", "", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("pc_effect_rate", "配置生效率", true, "", "", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("pc_business_rate", "业务生效率", true, "", "", VTYPE_FLOAT, "百分比"));
		ops.add(new Option("pc_rate_fin", "总体生效率", true, "", "", VTYPE_FLOAT, "百分比"));
		return ops;
	}
	
	public static Object EY_BAR_BAR_AUDIT() {
		
		List<Option> ops = new ArrayList<Option>();
		
		ops.add(new Option("total_score", "合规总分"));
		ops.add(new Option("compt_score", "竞品健康度评分"));
		ops.add(new Option("killad_score", "去广告/屏蔽行为评分"));
		ops.add(new Option("active_score", "活跃度评分"));
		ops.add(new Option("pro_score", "产品健康度评分"));
		
		return ops;
	}
	
	public static Object EY_BAR_BAR_CHANNEL() {
		
		List<Option> ops = new ArrayList<Option>();
		
		ops.add(new Option("total_score", "质量总分"));
		ops.add(new Option("eyrun_score", "EYRUN评分"));
		ops.add(new Option("tpsp_score", "TPSP评分"));
		ops.add(new Option("center_score", "应用中心评分"));
		ops.add(new Option("youtop_score", "友拓评分"));
		
		return ops;
	}
	
	
	
	public static void main(String[] args) {
		
		List<Option> ops = EY_GLOBAL_BASIC_PROFILE();
		System.out.println(JSON.toJSONString(ops));
	}


}
