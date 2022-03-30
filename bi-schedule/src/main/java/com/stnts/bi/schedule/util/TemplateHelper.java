package com.stnts.bi.schedule.util;

import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;

/**
 * @author liutianyuan
 */

public class TemplateHelper {

    private static TemplateEngine sdkEngine = TemplateUtil.createEngine(new TemplateConfig("template/sdk", TemplateConfig.ResourceMode.CLASSPATH));

    public static TemplateEngine getSdkTemplateEngine() {
        return sdkEngine;
    }
    
    /**
     *   商业插件sql脚本模板
     */
    private static TemplateEngine pluginEngine = TemplateUtil.createEngine(new TemplateConfig("template/plugin", TemplateConfig.ResourceMode.CLASSPATH));
    public static TemplateEngine getPluginTemplateEngine() {
    	return pluginEngine;
    }

    /**
     *  扣量sql脚本模板
     */
    private static TemplateEngine deductEngine = TemplateUtil.createEngine(new TemplateConfig("template/deduct", TemplateConfig.ResourceMode.CLASSPATH));

    public static TemplateEngine getDeductTemplateEngine() {
        return deductEngine;
    }
}
