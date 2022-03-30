package com.stnts.bi.sys.utils;

import cn.hutool.core.util.StrUtil;
import org.apache.commons.lang3.StringUtils;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.stnts.bi.sys.common.Constants;

/**
 * @author liang.zhang
 * @date 2020年3月26日
 * @desc TODO
 */
public class SysUtil implements Constants{
	
	public static String toLike(String param) {
		return StringUtils.isBlank(param) ? param : LIKE_SYMBOL.concat(escapeChar(param)).concat(LIKE_SYMBOL);
	}
	
	public static String toLikeSuffif(String param) {
		return StringUtils.isBlank(param) ? param : param.concat("%");
	}
	
	public static <T> Page<T> toPage(Integer page, int pageSize) {
		int _pageNo = null == page ? 1 : page;
		return new Page<T>(_pageNo, pageSize);
	}
	
	//mysql的模糊查询时特殊字符转义
    public static String escapeChar(String before){
        if(StringUtils.isNotBlank(before)){
            before = before.replaceAll("\\\\", "\\\\\\\\");
            before = before.replaceAll("_", "\\\\_");
            before = before.replaceAll("%", "\\\\%");
        }
        return before ;
    }

    public static String dmIndex(String pid, String id){
		return StrUtil.concat(true, pid, "-", id);
	}
}
