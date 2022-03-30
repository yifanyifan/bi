package com.stnts.bi.plugin.special;

import java.util.HashMap;
import java.util.Map;

import com.stnts.bi.plugin.special.handler.Handler;
import com.stnts.bi.plugin.special.handler.PluginCross4Channel;
import com.stnts.bi.plugin.special.handler.PluginCross4MultiDim;
import com.stnts.bi.plugin.special.handler.PluginCross4Plugin;
import com.stnts.bi.plugin.special.handler.PluginErrCardHandler;
import com.stnts.bi.plugin.special.handler.PluginErrDetailHandler;
import com.stnts.bi.plugin.special.handler.PluginErrHandler;
import com.stnts.bi.plugin.special.handler.PluginErrTableHandler;
import com.stnts.bi.plugin.special.handler.PluginErrTrendHandler;
import com.stnts.bi.plugin.special.handler.PluginTopHandler;
import com.stnts.bi.plugin.special.handler.PluginTransDisHandler;
import com.stnts.bi.plugin.special.handler.PluginTransHandler;
import com.stnts.bi.plugin.special.handler.PluginTrendCardHandler;
import com.stnts.bi.plugin.special.handler.PluginTrendHandler;

/**
 * @author liang.zhang
 * @date 2020年7月7日
 * @desc TODO
 */
public class ServiceHandlerFactory {
	
	public static final Map<String, Handler> handlers = new HashMap<String, Handler>();
	
	static {
		
		/** 001 覆盖：渠道排名，插件排名 */
		handlers.put(PluginTopHandler.HANDLER_ID, new PluginTopHandler());
		/** 002 覆盖：卡片*/
		handlers.put(PluginTrendCardHandler.HANDLER_ID, new PluginTrendCardHandler());
		/** 009 覆盖：数据趋势 */
		
		/** 011 渠道交叉  */
		handlers.put(PluginCross4Channel.HANDLER_ID, new PluginCross4Channel());
		/** 012  插件交叉  */
		handlers.put(PluginCross4Plugin.HANDLER_ID, new PluginCross4Plugin());
		/** 013 多维交叉 */
		handlers.put(PluginCross4MultiDim.HANDLER_ID, new PluginCross4MultiDim());
		
		handlers.put(PluginTrendHandler.HANDLER_ID, new PluginTrendHandler());
		/** 003 转化：*/
		handlers.put(PluginTransHandler.HANDLER_ID, new PluginTransHandler());
		/** 004 转化-分布  */
		handlers.put(PluginTransDisHandler.HANDLER_ID, new PluginTransDisHandler());
		/** 005 异常分析: 卡片  */
		handlers.put(PluginErrCardHandler.HANDLER_ID, new PluginErrCardHandler());
		/** 006 异常-趋势  */
		handlers.put(PluginErrTrendHandler.HANDLER_ID, new PluginErrTrendHandler());
		/** 010 异常分析-数据明细  */
		handlers.put(PluginErrTableHandler.HANDLER_ID, new PluginErrTableHandler());
		/** 008 异常明细-表格  */
		handlers.put(PluginErrHandler.HANDLER_ID, new PluginErrHandler());
		/** 007 异常明细:饼图+趋势  */
		handlers.put(PluginErrDetailHandler.HANDLER_ID, new PluginErrDetailHandler());
		
	}
	
	public static Handler getHandler(String id) {
		return handlers.get(id);
	}
}
