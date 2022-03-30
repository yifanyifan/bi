package com.stnts.bi.sys.utils;

import com.stnts.bi.sys.common.Constants;
import org.apache.commons.lang3.StringUtils;

import java.util.Arrays;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/12
 */
public class OlapPermIdUtil implements Constants {

    /**
     * 生成olap过来的主键ID
     * @param rootId 模块ID
     * @param permType
     * @param olapPermId
     * @return
     */
    public static String initId(String rootId, Integer permType, Integer olapPermId){
        String key = permType == 1 ? KEY_MENU : KEY_PAGE;
        return StringUtils.join(Arrays.asList(rootId, key, olapPermId), "_");
    }

    /**
     * 生成默认目录
     * @param rootId
     * @return
     */
    public static String initId(String rootId){
        return StringUtils.join(KEY_DEFAULT, rootId);
    }
}
