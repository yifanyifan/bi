package com.stnts.bi.datamanagement.util;

import cn.hutool.extra.template.TemplateConfig;
import cn.hutool.extra.template.TemplateEngine;
import cn.hutool.extra.template.TemplateUtil;

/**
 * @author liutianyuan
 * @date 2019-05-10 18:44
 */

public class TemplateHelper {

    private static TemplateEngine engine = TemplateUtil.createEngine(new TemplateConfig("template", TemplateConfig.ResourceMode.CLASSPATH));

    public static TemplateEngine getTemplateEngine() {
        return engine;
    }

}
