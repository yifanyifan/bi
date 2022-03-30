package com.stnts.tc.common;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 处理一些KEY
 * @author liang.zhang
 * @date 2019年12月11日
 * @desc TODO
 */
public class KeyUtils implements JsonDict{
	
	/**
	 * 网吧详情基本信息(单独表)
	 * @param gid
	 * @return
	 */
	public static String B_DE_BA(String gid) {
		return String.format("B_DE_BA_%s", gid);
	}
	
	/**
	 * 网吧环境信息
	 * @param year
	 * @param gid
	 * @return
	 */
	public static String B_DE_ENV(String year, String gid) {
		return String.format("%s_B_DE_ENV_%s_V_D", year, gid);
	}
	
	/**
	 * 网吧指标 （这里只返回关键部分 不含年份和值类型及维度）
	 * @param gid
	 * @return
	 */
	public static String B_DE_K(String gid) {
		return String.format("B_DE_K_%s", gid);
	}
	
	/**
	 * 网吧通道 （这里只返回关键部分 不含年份和值类型及维度）
	 * @param gid
	 * @return
	 */
	public static String B_DE_C(String gid) {
		return String.format("B_DE_C_%s", gid);
	}
	
	/**
	 * 网吧插件 （这里只返回关键部分 不含年份和值类型及维度）
	 * @param gid
	 * @return
	 */
	public static String B_DE_PI(String gid) {
		return String.format("B_DE_PI_%s", gid);
	}
	
	/**
	 * 网吧插件 （这里只返回关键部分 不含年份和值类型及维度）
	 * @param gid
	 * @return
	 */
	public static String B_DE_PI_DE(String gid) {
		return String.format("B_DE_PI_DE_%s", gid);
	}
	
	/**
	 * 网吧评分 合规 （这里只返回关键部分 不含年份和值类型及维度）
	 * @param gid
	 * @return
	 */
	public static String B_DE_S_AU(String gid) {
		return String.format("B_DE_S_AU_%s", gid);
	}
	
	/**
	 * 网吧评分 通道（这里只返回关键部分 不含年份和值类型及维度）
	 * @param gid
	 * @return
	 */
	public static String B_DE_S_C(String gid) {
		return String.format("B_DE_S_C_%s", gid);
	}
	
	/**
	 *全局插件KEY（这里只返回关键部分 不含年份和值类型及维度）
	 * @param pid
	 * @return
	 */
	public static String PI(String pid) {
		return String.format("PI_%s", pid);
	}
	
	/**
	 * 
	 * @param isComp 
	 * @return
	 */
	public static List<String> B_DE_K_LIST(boolean isComp){
		
		List<String> list = new ArrayList<>();
		list.add(B_DE_K_PC_START);
		if(!isComp) {
			list.add(B_DE_K_PC_START_TV);
			list.add(B_DE_K_PC_START_TR);
			list.add(B_DE_K_PC_START_HV);
			list.add(B_DE_K_PC_START_HR);
		}
		list.add(B_DE_K_PC_BUILD);
		if(!isComp) {
			list.add(B_DE_K_PC_BUILD_TV);
			list.add(B_DE_K_PC_BUILD_TR);
			list.add(B_DE_K_PC_BUILD_HV);
			list.add(B_DE_K_PC_BUILD_HR);
		}
		list.add(B_DE_K_SZL);
		if(!isComp) {
			list.add(B_DE_K_SZL_TV);
			list.add(B_DE_K_SZL_TR);
			list.add(B_DE_K_SZL_HV);
			list.add(B_DE_K_SZL_HR);
		}
		list.add(B_DE_K_PC_ACTIVE);
		if(!isComp) {
			list.add(B_DE_K_PC_ACTIVE_TV);
			list.add(B_DE_K_PC_ACTIVE_TR);
			list.add(B_DE_K_PC_ACTIVE_HV);
			list.add(B_DE_K_PC_ACTIVE_HR);
		}
		list.add(B_DE_K_PC_BASE);
		if(!isComp) {
			list.add(B_DE_K_PC_BASE_TV);
			list.add(B_DE_K_PC_BASE_TR);
			list.add(B_DE_K_PC_BASE_HV);
			list.add(B_DE_K_PC_BASE_HR);
		}
		list.add(B_DE_K_PC_PRODUCT);
		if(!isComp) {
			list.add(B_DE_K_PC_PRODUCT_TV);
			list.add(B_DE_K_PC_PRODUCT_TR);
			list.add(B_DE_K_PC_PRODUCT_HV);
			list.add(B_DE_K_PC_PRODUCT_HR);
		}
		list.add(B_DE_K_RES_NUM);
		if(!isComp) {
			list.add(B_DE_K_RES_NUM_TV);
			list.add(B_DE_K_RES_NUM_TR);
			list.add(B_DE_K_RES_NUM_HV);
			list.add(B_DE_K_RES_NUM_HR);
		}
		list.add(B_DE_K_RES_DOWN);
		if(!isComp) {
			list.add(B_DE_K_RES_DOWN_TV);
			list.add(B_DE_K_RES_DOWN_TR);
			list.add(B_DE_K_RES_DOWN_HV);
			list.add(B_DE_K_RES_DOWN_HR);
		}
		list.add(B_DE_K_RES_BOOT);
		if(!isComp) {
			list.add(B_DE_K_RES_BOOT_TV);
			list.add(B_DE_K_RES_BOOT_TR);
			list.add(B_DE_K_RES_BOOT_HV);
			list.add(B_DE_K_RES_BOOT_HR);
		}
		
		return list;
	}
	
	/**
	 * @param isComp
	 * @return
	 */
	public static List<String> B_DE_C_LIST(boolean isComp){
		
		List<String> list = new ArrayList<>();
		list.add(B_DE_C_START_RATE_EYRUN);
		if(!isComp) {
			list.add(B_DE_C_START_RATE_EYRUN_TV);
			list.add(B_DE_C_START_RATE_EYRUN_TR);
			list.add(B_DE_C_START_RATE_EYRUN_HV);
			list.add(B_DE_C_START_RATE_EYRUN_HR);
			list.add(B_DE_C_START_RATE_EYRUN_AV);
			list.add(B_DE_C_START_RATE_EYRUN_AR);
		}
		
		list.add(B_DE_C_START_RATE_TPSP);
		if(!isComp) {
			list.add(B_DE_C_START_RATE_TPSP_TV);
			list.add(B_DE_C_START_RATE_TPSP_TR);
			list.add(B_DE_C_START_RATE_TPSP_HV);
			list.add(B_DE_C_START_RATE_TPSP_HR);
			list.add(B_DE_C_START_RATE_TPSP_AV);
			list.add(B_DE_C_START_RATE_TPSP_AR);
		}
		list.add(B_DE_C_START_RATE_CENTER);
		if(!isComp) {
			list.add(B_DE_C_START_RATE_CENTER_TV);
			list.add(B_DE_C_START_RATE_CENTER_TR);
			list.add(B_DE_C_START_RATE_CENTER_HV);
			list.add(B_DE_C_START_RATE_CENTER_HR);
			list.add(B_DE_C_START_RATE_CENTER_AV);
			list.add(B_DE_C_START_RATE_CENTER_AR);
		}
		list.add(B_DE_C_SUV_RATE_EYRUN);
		if(!isComp) {
			list.add(B_DE_C_SUV_RATE_EYRUN_TV);
			list.add(B_DE_C_SUV_RATE_EYRUN_TR);
			list.add(B_DE_C_SUV_RATE_EYRUN_HV);
			list.add(B_DE_C_SUV_RATE_EYRUN_HR);
			list.add(B_DE_C_SUV_RATE_EYRUN_AV);
			list.add(B_DE_C_SUV_RATE_EYRUN_AR);
		}
		list.add(B_DE_C_SUV_RATE_TPSP);
		if(!isComp) {
			list.add(B_DE_C_SUV_RATE_TPSP_TV);
			list.add(B_DE_C_SUV_RATE_TPSP_TR);
			list.add(B_DE_C_SUV_RATE_TPSP_HV);
			list.add(B_DE_C_SUV_RATE_TPSP_HR);
			list.add(B_DE_C_SUV_RATE_TPSP_AV);
			list.add(B_DE_C_SUV_RATE_TPSP_AR);
		}
		list.add(B_DE_C_SUV_RATE_CENTER);
		if(!isComp) {
			list.add(B_DE_C_SUV_RATE_CENTER_TV);
			list.add(B_DE_C_SUV_RATE_CENTER_TR);
			list.add(B_DE_C_SUV_RATE_CENTER_HV);
			list.add(B_DE_C_SUV_RATE_CENTER_HR);
			list.add(B_DE_C_SUV_RATE_CENTER_AV);
			list.add(B_DE_C_SUV_RATE_CENTER_AR);
		}
		list.add(B_DE_C_REQ_RATE_YOUTOP);
		if(!isComp) {
			list.add(B_DE_C_REQ_RATE_YOUTOP_TV);
			list.add(B_DE_C_REQ_RATE_YOUTOP_TR);
			list.add(B_DE_C_REQ_RATE_YOUTOP_HV);
			list.add(B_DE_C_REQ_RATE_YOUTOP_HR);
			list.add(B_DE_C_REQ_RATE_YOUTOP_AV);
			list.add(B_DE_C_REQ_RATE_YOUTOP_AR);
		}
		list.add(B_DE_C_PV_RATE_YOUTOP);
		if(!isComp) {
			list.add(B_DE_C_PV_RATE_YOUTOP_TV);
			list.add(B_DE_C_PV_RATE_YOUTOP_TR);
			list.add(B_DE_C_PV_RATE_YOUTOP_HV);
			list.add(B_DE_C_PV_RATE_YOUTOP_HR);
			list.add(B_DE_C_PV_RATE_YOUTOP_AV);
			list.add(B_DE_C_PV_RATE_YOUTOP_AR);
		}
		return list;
	}
	
	/**
	 * 网吧插件概况全局部分
	 * @param isComp
	 * @return
	 */
	public static List<String> B_DE_PI_LIST(boolean isComp){
		
		List<String> list = new ArrayList<>();
		
	    list.add(B_DE_PI_BIZ_PC_PLUG_COVER);
	    if(!isComp) {
		    list.add(B_DE_PI_BIZ_PC_PLUG_COVER_TV);
		    list.add(B_DE_PI_BIZ_PC_PLUG_COVER_TR);
		    list.add(B_DE_PI_BIZ_PC_PLUG_COVER_HV);
		    list.add(B_DE_PI_BIZ_PC_PLUG_COVER_HR);
	    }
	    list.add(B_DE_PI_BIZ_NUM_PLUG);
//	    if(!isComp) {
//		    list.add(B_DE_PI_BIZ_NUM_PLUG_TV);
//		    list.add(B_DE_PI_BIZ_NUM_PLUG_TR);
//		    list.add(B_DE_PI_BIZ_NUM_PLUG_HV);
//		    list.add(B_DE_PI_BIZ_NUM_PLUG_HR);
//	    }
		list.add(B_DE_PI_BASE_PC_PLUG_COVER);
		if(!isComp) {
			list.add(B_DE_PI_BASE_PC_PLUG_COVER_TV);
			list.add(B_DE_PI_BASE_PC_PLUG_COVER_TR);
			list.add(B_DE_PI_BASE_PC_PLUG_COVER_HV);
			list.add(B_DE_PI_BASE_PC_PLUG_COVER_HR);
		}
		list.add(B_DE_PI_BASE_NUM_PLUG);
//		if(!isComp) {
//			list.add(B_DE_PI_BASE_NUM_PLUG_TV);
//			list.add(B_DE_PI_BASE_NUM_PLUG_TR);
//			list.add(B_DE_PI_BASE_NUM_PLUG_HV);
//			list.add(B_DE_PI_BASE_NUM_PLUG_HR);
//		}
		return list;
	}
	
	/**
	 * 网吧插件列表部分
	 * @param isComp
	 * @return
	 */
	public static List<String> B_DE_PI_DE_LIST(boolean isComp){
		
		List<String> list = new ArrayList<>();
		list.add(B_DE_PI_DE_PLUGIN_ID);
		list.add(B_DE_PI_DE_PLUGIN_NAME);
		
		list.add(B_DE_PI_DE_PC_ACTIVE);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_ACTIVE_TV);
			list.add(B_DE_PI_DE_PC_ACTIVE_TR);
			list.add(B_DE_PI_DE_PC_ACTIVE_HV);
			list.add(B_DE_PI_DE_PC_ACTIVE_HR);
		}
		list.add(B_DE_PI_DE_PC_REACH);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_REACH_TV);
			list.add(B_DE_PI_DE_PC_REACH_TR);
			list.add(B_DE_PI_DE_PC_REACH_HV);
			list.add(B_DE_PI_DE_PC_REACH_HR);
		}
		list.add(B_DE_PI_DE_PC_START);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_START_TV);
			list.add(B_DE_PI_DE_PC_START_TR);
			list.add(B_DE_PI_DE_PC_START_HV);
			list.add(B_DE_PI_DE_PC_START_HR);
		}
		list.add(B_DE_PI_DE_PC_EFFECT);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_EFFECT_TV);
			list.add(B_DE_PI_DE_PC_EFFECT_TR);
			list.add(B_DE_PI_DE_PC_EFFECT_HV);
			list.add(B_DE_PI_DE_PC_EFFECT_HR);
		}
		list.add(B_DE_PI_DE_PC_BUSINESS);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_BUSINESS_TV);
			list.add(B_DE_PI_DE_PC_BUSINESS_TR);
			list.add(B_DE_PI_DE_PC_BUSINESS_HV);
			list.add(B_DE_PI_DE_PC_BUSINESS_HR);
		}
		list.add(B_DE_PI_DE_PC_REACH_RATE);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_REACH_RATE_TV);
			list.add(B_DE_PI_DE_PC_REACH_RATE_TR);
			list.add(B_DE_PI_DE_PC_REACH_RATE_HV);
			list.add(B_DE_PI_DE_PC_REACH_RATE_HR);
		}
		list.add(B_DE_PI_DE_PC_START_RATE);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_START_RATE_TV);
			list.add(B_DE_PI_DE_PC_START_RATE_TR);
			list.add(B_DE_PI_DE_PC_START_RATE_HV);
			list.add(B_DE_PI_DE_PC_START_RATE_HR);
		}
		list.add(B_DE_PI_DE_PC_EFFECT_RATE);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_EFFECT_RATE_TV);
			list.add(B_DE_PI_DE_PC_EFFECT_RATE_TR);
			list.add(B_DE_PI_DE_PC_EFFECT_RATE_HV);
			list.add(B_DE_PI_DE_PC_EFFECT_RATE_HR);
		}
		list.add(B_DE_PI_DE_PC_BUSINESS_RATE);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_BUSINESS_RATE_TV);
			list.add(B_DE_PI_DE_PC_BUSINESS_RATE_TR);
			list.add(B_DE_PI_DE_PC_BUSINESS_RATE_HV);
			list.add(B_DE_PI_DE_PC_BUSINESS_RATE_HR);
		}
		list.add(B_DE_PI_DE_PC_RATE_FIN);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_RATE_FIN_TV);
			list.add(B_DE_PI_DE_PC_RATE_FIN_TR);
			list.add(B_DE_PI_DE_PC_RATE_FIN_HV);
			list.add(B_DE_PI_DE_PC_RATE_FIN_HR);
		}
		return list;
	}
	
	/**
	 * 全局网吧插件列表部分
	 * @param isComp
	 * @return
	 */
	public static List<String> PI_LIST(boolean isComp){
		
		List<String> list = new ArrayList<>();
		list.add(B_DE_PI_DE_PLUGIN_ID);
		list.add(B_DE_PI_DE_PLUGIN_NAME);
		
		list.add(PI_BAR_ACTIVE);
		if(!isComp) {
			list.add(PI_BAR_ACTIVE_TV);
			list.add(PI_BAR_ACTIVE_TR);
			list.add(PI_BAR_ACTIVE_HV);
			list.add(PI_BAR_ACTIVE_HR);
		}
		list.add(PI_BAR_REACH);
		if(!isComp) {
			list.add(PI_BAR_REACH_TV);
			list.add(PI_BAR_REACH_TR);
			list.add(PI_BAR_REACH_HV);
			list.add(PI_BAR_REACH_HR);
		}
		list.add(PI_BAR_START);
		if(!isComp) {
			list.add(PI_BAR_START_TV);
			list.add(PI_BAR_START_TR);
			list.add(PI_BAR_START_HV);
			list.add(PI_BAR_START_HR);
		}
		list.add(PI_BAR_EFFECT);
		if(!isComp) {
			list.add(PI_BAR_EFFECT_TV);
			list.add(PI_BAR_EFFECT_TR);
			list.add(PI_BAR_EFFECT_HV);
			list.add(PI_BAR_EFFECT_HR);
		}
		list.add(PI_BAR_BUSINESS);
		if(!isComp) {
			list.add(PI_BAR_BUSINESS_TV);
			list.add(PI_BAR_BUSINESS_TR);
			list.add(PI_BAR_BUSINESS_HV);
			list.add(PI_BAR_BUSINESS_HR);
		}
		list.add(B_DE_PI_DE_PC_ACTIVE);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_ACTIVE_TV);
			list.add(B_DE_PI_DE_PC_ACTIVE_TR);
			list.add(B_DE_PI_DE_PC_ACTIVE_HV);
			list.add(B_DE_PI_DE_PC_ACTIVE_HR);
		}
		list.add(B_DE_PI_DE_PC_REACH);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_REACH_TV);
			list.add(B_DE_PI_DE_PC_REACH_TR);
			list.add(B_DE_PI_DE_PC_REACH_HV);
			list.add(B_DE_PI_DE_PC_REACH_HR);
		}
		list.add(B_DE_PI_DE_PC_START);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_START_TV);
			list.add(B_DE_PI_DE_PC_START_TR);
			list.add(B_DE_PI_DE_PC_START_HV);
			list.add(B_DE_PI_DE_PC_START_HR);
		}
		list.add(B_DE_PI_DE_PC_EFFECT);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_EFFECT_TV);
			list.add(B_DE_PI_DE_PC_EFFECT_TR);
			list.add(B_DE_PI_DE_PC_EFFECT_HV);
			list.add(B_DE_PI_DE_PC_EFFECT_HR);
		}
		list.add(B_DE_PI_DE_PC_BUSINESS);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_BUSINESS_TV);
			list.add(B_DE_PI_DE_PC_BUSINESS_TR);
			list.add(B_DE_PI_DE_PC_BUSINESS_HV);
			list.add(B_DE_PI_DE_PC_BUSINESS_HR);
		}
		list.add(B_DE_PI_DE_PC_REACH_RATE);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_REACH_RATE_TV);
			list.add(B_DE_PI_DE_PC_REACH_RATE_TR);
			list.add(B_DE_PI_DE_PC_REACH_RATE_HV);
			list.add(B_DE_PI_DE_PC_REACH_RATE_HR);
		}
		list.add(B_DE_PI_DE_PC_START_RATE);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_START_RATE_TV);
			list.add(B_DE_PI_DE_PC_START_RATE_TR);
			list.add(B_DE_PI_DE_PC_START_RATE_HV);
			list.add(B_DE_PI_DE_PC_START_RATE_HR);
		}
		list.add(B_DE_PI_DE_PC_EFFECT_RATE);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_EFFECT_RATE_TV);
			list.add(B_DE_PI_DE_PC_EFFECT_RATE_TR);
			list.add(B_DE_PI_DE_PC_EFFECT_RATE_HV);
			list.add(B_DE_PI_DE_PC_EFFECT_RATE_HR);
		}
		list.add(B_DE_PI_DE_PC_BUSINESS_RATE);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_BUSINESS_RATE_TV);
			list.add(B_DE_PI_DE_PC_BUSINESS_RATE_TR);
			list.add(B_DE_PI_DE_PC_BUSINESS_RATE_HV);
			list.add(B_DE_PI_DE_PC_BUSINESS_RATE_HR);
		}
		list.add(B_DE_PI_DE_PC_RATE_FIN);
		if(!isComp) {
			list.add(B_DE_PI_DE_PC_RATE_FIN_TV);
			list.add(B_DE_PI_DE_PC_RATE_FIN_TR);
			list.add(B_DE_PI_DE_PC_RATE_FIN_HV);
			list.add(B_DE_PI_DE_PC_RATE_FIN_HR);
		}
		return list;
	}
	
	/**
	 * 网吧合规评分指标项
	 * @param isComp
	 * @return
	 */
	public static List<String> B_S_AU_LIST(boolean isComp){
		
		List<String> list = new ArrayList<String>();
		list.add(B_S_AU_TOTAL_SCORE);
		if(!isComp) {
			list.add(B_S_AU_TOTAL_SCORE_TV);
			list.add(B_S_AU_TOTAL_SCORE_TR);
			list.add(B_S_AU_TOTAL_SCORE_HV);
			list.add(B_S_AU_TOTAL_SCORE_HR);
		}
		list.add(B_S_AU_COMPT_SCORE);
		if(!isComp) {
			list.add(B_S_AU_COMPT_SCORE_TV);
			list.add(B_S_AU_COMPT_SCORE_TR);
			list.add(B_S_AU_COMPT_SCORE_HV);
			list.add(B_S_AU_COMPT_SCORE_HR);
		}
		list.add(B_S_AU_KILLAD_SCORE);
		if(!isComp) {
			list.add(B_S_AU_KILLAD_SCORE_TV);
			list.add(B_S_AU_KILLAD_SCORE_TR);
			list.add(B_S_AU_KILLAD_SCORE_HV);
			list.add(B_S_AU_KILLAD_SCORE_HR);
		}
		list.add(B_S_AU_ACTIVE_SCORE);
		if(!isComp) {
			list.add(B_S_AU_ACTIVE_SCORE_TV);
			list.add(B_S_AU_ACTIVE_SCORE_TR);
			list.add(B_S_AU_ACTIVE_SCORE_HV);
			list.add(B_S_AU_ACTIVE_SCORE_HR);
		}
		list.add(B_S_AU_PRO_SCORE);
		if(!isComp) {
			list.add(B_S_AU_PRO_SCORE_TV);
			list.add(B_S_AU_PRO_SCORE_TR);
			list.add(B_S_AU_PRO_SCORE_HV);
			list.add(B_S_AU_PRO_SCORE_HR);
		}
		return list;
	}
	
	public static List<String> B_S_C_LIST(boolean isComp){
		
		List<String> list = new ArrayList<String>();
		list.add(B_S_C_TOTAL_SCORE);
		if(!isComp) {
			list.add(B_S_C_TOTAL_SCORE_TV);
			list.add(B_S_C_TOTAL_SCORE_TR);
			list.add(B_S_C_TOTAL_SCORE_HV);
			list.add(B_S_C_TOTAL_SCORE_HR);
		}
		list.add(B_S_C_EYRUN_SCORE);
		if(!isComp) {
			list.add(B_S_C_EYRUN_SCORE_TV);
			list.add(B_S_C_EYRUN_SCORE_TR);
			list.add(B_S_C_EYRUN_SCORE_HV);
			list.add(B_S_C_EYRUN_SCORE_HR);
		}
		list.add(B_S_C_TPSP_SCORE);
		if(!isComp) {
			list.add(B_S_C_TPSP_SCORE_TV);
			list.add(B_S_C_TPSP_SCORE_TR);
			list.add(B_S_C_TPSP_SCORE_HV);
			list.add(B_S_C_TPSP_SCORE_HR);
		}
		list.add(B_S_C_APPCENTER_SCORE);
		if(!isComp) {
			list.add(B_S_C_APPCENTER_SCORE_TV);
			list.add(B_S_C_APPCENTER_SCORE_TR);
			list.add(B_S_C_APPCENTER_SCORE_HV);
			list.add(B_S_C_APPCENTER_SCORE_HR);
		}
		list.add(B_S_C_YOUTOP_SCORE);
		if(!isComp) {
			list.add(B_S_C_YOUTOP_SCORE_TV);
			list.add(B_S_C_YOUTOP_SCORE_TR);
			list.add(B_S_C_YOUTOP_SCORE_HV);
			list.add(B_S_C_YOUTOP_SCORE_HR);
		}
		return list;
	}
	
	/**
	 * 易游 基础 通道 数据结构改动, 需要把网吧6月均值与通道数据json体中key做映射
	 * AV key -> V key
	 * @return
	 */
	public static Map<String, String> EY_BAR_CHL_MAP(){
		
		Map<String, String> map = new HashMap<String, String>();
		map.put("C_O_R_ER_AV", "start_rate_eyrun");
		map.put("C_O_R_TP_AV", "start_rate_tpsp");
		map.put("C_O_R_AP_AV", "start_rate_center");
		map.put("C_AL_R_ER_AV", "suv_rate_eyrun");
		map.put("C_AL_R_TP_AV", "suv_rate_tpsp");
		map.put("C_AL_R_AP_AV", "suv_rate_center");
		map.put("Y_REQ_R_AV", "req_rate_youtop");
		map.put("Y_CPM_R_AV", "pv_rate_youtop");
		return map;
	}
	
	/**
	 * 这里跟之前的风格有点违和，数据已经写了  就这样吧
	 * @param gid
	 * @return
	 */
	public static List<String> EY_BAR_CHL_LIST(String gid){
		
		List<String> kpis = new ArrayList<String>();
		kpis.add("C_O_R_ER".concat("_").concat(gid));
		kpis.add("C_O_R_TP".concat("_").concat(gid));
		kpis.add("C_O_R_AP".concat("_").concat(gid));
		kpis.add("C_AL_R_ER".concat("_").concat(gid));
		kpis.add("C_AL_R_TP".concat("_").concat(gid));
		kpis.add("C_AL_R_AP".concat("_").concat(gid));
		kpis.add("Y_REQ_R".concat("_").concat(gid));
		kpis.add("Y_CPM_R".concat("_").concat(gid));
		
		return kpis;
	}
}
