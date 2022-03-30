package com.stnts.bi.authorization;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2020/12/8
 */
public enum AuthCodeEnum {

    /**
     * 主仪表盘
     */
    DASHBOARD_BIZ_GAMEOP_INCOME_VIEW("主仪表盘-业务大盘-游戏运营-收入分析-浏览", "dashboard:biz:gameop:income:view"),
    DASHBOARD_BIZ_GAMEOP_INCOME_EXPORT("主仪表盘-业务大盘-游戏运营-收入分析-导出", "dashboard:biz:gameop:income:export"),
    DASHBOARD_BIZ_GAMEOP_USER_VIEW("主仪表盘-业务大盘-游戏运营-用户分析-浏览", "dashboard:biz:gameop:user:view"),
    DASHBOARD_BIZ_GAMEOP_USER_EXPORT("主仪表盘-业务大盘-游戏运营-用户分析-导出", "dashboard:biz:gameop:user:export"),
    DASHBOARD_BIZ_GAMEOP_PRODUCT_VIEW("主仪表盘-业务大盘-游戏运营-产品分析-浏览", "dashboard:biz:gameop:product:view"),
    DASHBOARD_BIZ_GAMEOP_PRODUCT_EXPORT("主仪表盘-业务大盘-游戏运营-产品分析-导出", "dashboard:biz:gameop:product:export"),
    DASHBOARD_BIZ_GAMEOP_CHANNEL_VIEW("主仪表盘-业务大盘-游戏运营-渠道分析-浏览", "dashboard:biz:gameop:channel:view"),
    DASHBOARD_BIZ_GAMEOP_CHANNEL_EXPORT("主仪表盘-业务大盘-游戏运营-渠道分析-导出", "dashboard:biz:gameop:channel:export"),
    //
    DASHBOARD_BIZ_DDPW_INCOME_VIEW("主仪表盘-业务大盘-带带陪玩-收入分析-浏览", "dashboard:biz:ddpw:income:view"),
    DASHBOARD_BIZ_DDPW_INCOME_EXPORT("主仪表盘-业务大盘-带带陪玩-收入分析-导出", "dashboard:biz:ddpw:income:export"),
    DASHBOARD_BIZ_DDPW_USER_VIEW("主仪表盘-业务大盘-带带陪玩-用户分析-浏览", "dashboard:biz:ddpw:user:view"),
    DASHBOARD_BIZ_DDPW_USER_EXPORT("主仪表盘-业务大盘-带带陪玩-用户分析-导出", "dashboard:biz:ddpw:user:export"),
    //TODO 大神这里需要改PRODUCT为GOD
    DASHBOARD_BIZ_DDPW_PRODUCT_VIEW("主仪表盘-业务大盘-带带陪玩-大神分析-浏览", "dashboard:biz:ddpw:product:view"),
    DASHBOARD_BIZ_DDPW_PRODUCT_EXPORT("主仪表盘-业务大盘-带带陪玩-大神分析-导出", "dashboard:biz:ddpw:product:export"),
    DASHBOARD_BIZ_DDPW_CHANNEL_VIEW("主仪表盘-业务大盘-带带陪玩-渠道分析-浏览", "dashboard:biz:ddpw:channel:view"),
    DASHBOARD_BIZ_DDPW_CHANNEL_EXPORT("主仪表盘-业务大盘-带带陪玩-渠道分析-导出", "dashboard:biz:ddpw:channel:export"),

    /**
     * SDK
     */
    SDK_PROFILE_REALTIME_KEY_VIEW("SDK-数据概况-实时数据-关键指标-浏览", "sdk:profile:realtime:key:view"),
    SDK_PROFILE_REALTIME_KEY_EXPORT("SDK-数据概况-实时数据-关键指标-导出", "sdk:profile:realtime:key:export"),
    SDK_PROFILE_REALTIME_OP_VIEW("SDK-数据概况-实时数据-运营指标-浏览", "sdk:profile:realtime:op:view"),
    SDK_PROFILE_REALTIME_OP_EXPORT("SDK-数据概况-实时数据-运营指标-导出", "sdk:profile:realtime:op:export"),
    SDK_CHANNEL_ANALYZE_VIEW("SDK-渠道数据-渠道分析-浏览", "sdk:channel:analyze:view"),
    SDK_CHANNEL_ANALYZE_EXPORT("SDK-渠道数据-渠道分析-浏览-导出", "sdk:channel:analyze:export"),




    /**
     *  易游
     */
    EY_GLOBAL_ACTIVE_VIEW("易游-全国-活跃数据-浏览", "ey:global:active:view"),
    EY_GLOBAL_ACTIVE_EXPORT("易游-全国-活跃数据-导出", "ey:global:active:export"),
    EY_GLOBAL_NEWLY_VIEW("易游-全国-新增数据-浏览", "ey:global:newly:view"),
    EY_GLOBAL_NEWLY_EXPORT("易游-全国-新增数据-导出", "ey:global:newly:export"),
    EY_GLOBAL_RETAIN_VIEW("易游-全国-留存数据-浏览", "ey:global:retain:view"),
    EY_GLOBAL_RETAIN_EXPORT("易游-全国-留存数据-导出", "ey:global:retain:export"),
    EY_GLOBAL_CHANNEL_VIEW("易游-全国-通道数据-浏览", "ey:global:channel:view"),
    EY_GLOBAL_CHANNEL_EXPORT("易游-全国-通道数据-导出", "ey:global:channel:export"),
    EY_GLOBAL_PLUGIN_VIEW("易游-全国-插件数据-浏览", "ey:global:plugin:view"),
    EY_GLOBAL_PLUGIN_EXPORT("易游-全国-插件数据-导出", "ey:global:plugin:export"),
    EY_GLOBAL_SCORE_VIEW("易游-全国-网吧评分-浏览", "ey:global:score:view"),
    EY_GLOBAL_SCORE_EXPORT("易游-全国-网吧评分-导出", "ey:global:score:export"),
    //
    EY_BAR_INFO_VIEW("易游-网吧-网吧信息-浏览", "ey:bar:info:view"),
    EY_BAR_INFO_EXPORT("易游-网吧-网吧信息-导出", "ey:bar:info:export"),
    EY_BAR_ACTIVE_VIEW("易游-网吧-网吧活跃-浏览", "ey:bar:active:view"),
    EY_BAR_ACTIVE_EXPORT("易游-网吧-网吧活跃-导出", "ey:bar:active:export"),
    EY_BAR_CHANNEL_VIEW("易游-网吧-通道数据-浏览", "ey:bar:channel:view"),
    EY_BAR_CHANNEL_EXPORT("易游-网吧-通道数据-导出", "ey:bar:channel:export"),
    EY_BAR_PLUGIN_VIEW("易游-网吧-插件数据-浏览", "ey:bar:plugin:view"),
    EY_BAR_PLUGIN_EXPORT("易游-网吧-插件数据-导出", "ey:bar:plugin:export"),
    EY_BAR_SCORE_VIEW("易游-网吧-网吧评分-浏览", "ey:bar:score:view"),
    EY_BAR_SCORE_EXPORT("易游-网吧-网吧评分-导出", "ey:bar:score:export"),

    /**
     * 商业插件
     */
    BP_DASHBOARD_COVER_VIEW("商业插件-盘面-覆盖-浏览", "bp:dashboard:cover:view"),
    BP_DASHBOARD_COVER_EXPORT("商业插件-盘面-覆盖-导出", "bp:dashboard:cover:export"),
    BP_DASHBOARD_CROSS_VIEW("商业插件-盘面-交叉分析-浏览", "bp:dashboard:cross:view"),
    BP_DASHBOARD_CROSS_EXPORT("商业插件-盘面-交叉分析-导出", "bp:dashboard:cross:export"),
    BP_SUBJECT_TRANS_VIEW("商业插件-专题分析-转化-浏览", "bp:subject:trans:view"),
    BP_SUBJECT_TRANS_EXPORT("商业插件-专题分析-转化-导出", "bp:subject:trans:export"),
    BP_SUBJECT_ERR_VIEW("商业插件-专题分析-异常分析-浏览", "bp:subject:err:view"),
    BP_SUBJECT_ERR_EXPORT("商业插件-专题分析-异常分析-导出", "bp:subject:err:export"),
    BP_SUBJECT_ENV_VIEW("商业插件-专题分析-环境监测-浏览", "bp:subject:env:view"),
    BP_SUBJECT_ENV_EXPORT("商业插件-专题分析-环境监测-导出", "bp:subject:env:export"),

    /**
     * 数据管理
     */
    DM_KPI_CLASSIFY_VIEW("数据管理-绩效管理-业务考核分类-浏览", "dm:kpi:classify:view"),
    DM_KPI_CLASSIFY_ADD("数据管理-绩效管理-业务考核分类-新增", "dm:kpi:classify:add"),
    DM_KPI_CLASSIFY_ACTIVE("数据管理-绩效管理-业务考核分类-启用", "dm:kpi:classify:active"),
    DM_KPI_CLASSIFY_EDIT("数据管理-绩效管理-业务考核分类-编辑", "dm:kpi:classify:edit"),
    DM_KPI_CLASSIFY_DEL("数据管理-绩效管理-业务考核分类-删除", "dm:kpi:classify:del"),
    DM_PARTNER_ADD_VIEW("数据管理-合作方管理-新增合作方-浏览", "dm:partner:add:view"),
    DM_PARTNER_DICT_VIEW("数据管理-合作方管理-行业分类字典-浏览", "dm:partner:dict:view"),
    DM_PARTNER_DICT_ACTIVE("数据管理-合作方管理-行业分类字典-启用", "dm:partner:dict:active"),
    DM_PARTNER_INFO_FULLNAME_VIEW("数据管理-合作方管理-合作方信息-按全称汇总-浏览", "dm:partner:info:fullname:view"),
    DM_PARTNER_INFO_FULLNAME_ACTIVE("数据管理-合作方管理-合作方信息-按全称汇总-启用", "dm:partner:info:fullname:active"),
    DM_PARTNER_INFO_SHORTNAME_VIEW("数据管理-合作方管理-合作方信息-按简称汇总-浏览", "dm:partner:info:shortname:view"),
    DM_PARTNER_INFO_DETAIL_VIEW("数据管理-合作方管理-合作方信息-按来源明细-浏览", "dm:partner:info:detail:view"),
    DM_PARTNER_INFO_DETAIL_EDIT("数据管理-合作方管理-合作方信息-按来源明细-编辑", "dm:partner:info:detail:edit"),
    DM_PARTNER_INFO_DETAIL_DEL("数据管理-合作方管理-合作方信息-按来源明细-删除", "dm:partner:info:detail:del"),
    ;


    /**
     * 注释
     */
    String desc;

    /**
     * 权限code
     */
    String code;

    AuthCodeEnum(String desc, String code) {
        this.desc = desc;
        this.code = code;
    }

    public String getCode() {
        return code;
    }

    public String getDesc() {
        return desc;
    }
}
