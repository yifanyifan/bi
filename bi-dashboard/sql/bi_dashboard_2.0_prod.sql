drop TABLE bi_dashboard.st_game_operate_roi_statistic ON CLUSTER cluster_group01;

CREATE TABLE bi_dashboard.st_game_operate_roi_statistic ON CLUSTER cluster_group01(
    `date_time` Date COMMENT '时间',
    `group_name` String COMMENT '部门',
    `category1` String COMMENT '一级业务类别',
    `category2` String COMMENT '二级业务类别',
    `category3` String COMMENT '三级业务类别',
    `income_target_s` Float32 COMMENT '收入目标（S档）',
    `income_target_a` Float32 COMMENT '收入目标（A档）',
    `income_target_b` Float32 COMMENT '收入目标（B档）',
    `income_target_c` Float32 COMMENT '收入目标（C档）',
    `income_target_d` Float32 COMMENT '收入目标（D档）',
    `profit_target_s` Float32 COMMENT '利润目标（S档）',
    `profit_target_a` Float32 COMMENT '利润目标（A档）',
    `profit_target_b` Float32 COMMENT '利润目标（B档）',
    `profit_target_c` Float32 COMMENT '利润目标（C档）',
    `profit_target_d` Float32 COMMENT '利润目标（D档）',
    `customer_id` String COMMENT '合作方ID',
    `customer_name` String COMMENT '合作方名称',
    `cooperate_type` String COMMENT '合作方式',
    `agent_company_name` String COMMENT '媒体商公司名称',
    `agent_id` String COMMENT '媒体商',
    `game_category` UInt8 COMMENT '游戏类型',
    `game_category_name` String COMMENT '游戏类型名称',
    `game_code` String COMMENT '游戏code',
    `game_name` String COMMENT '游戏名称',
    `real_income` Float32 COMMENT '实际收入',
    `real_rmb_income` Float32 COMMENT '实际人民币',
    `real_coupon_income` Float32 COMMENT '实际E币券',
    `real_cost` Float32 COMMENT '实际成本',
    `real_cp_cost` Float32 COMMENT '实际CP成本',
    `real_channel_cost` Float32 COMMENT '实际渠道成本',
    `real_coupon_cost` Float32 COMMENT '实际E币券成本',
    `real_roi` Float32 COMMENT '实际ROI',
    `estimate_coupon_income` Float32 COMMENT '预估E币券',
    `estimate_rmb_income` Float32 COMMENT '预估人民币',
    `estimate_income` Float32 COMMENT '预估收入',
    `estimate_cost` Float32 COMMENT '预估成本',
    `estimate_cp_cost` Float32 COMMENT '预估CP成本',
    `estimate_channel_cost` Float32 COMMENT '预估渠道成本',
    `estimate_coupon_cost` Float32 COMMENT '预估E币券成本',
    `estimate_roi` Float32 COMMENT '预估ROI',
    `timeline` String COMMENT '分区字段'
) ENGINE = ReplicatedMergeTree(
    '/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/bi_dashboard/st_game_operate_roi_statistic',
    '{replica}'
) PARTITION BY timeline
ORDER BY
    (
        date_time,
        group_name,
        category1,
        category2,
        category3,
        income_target_s,
        income_target_a,
        income_target_b,
        income_target_c,
        income_target_d,
        profit_target_s,
        profit_target_a,
        profit_target_b,
        profit_target_c,
        profit_target_d,
        agent_id,
        game_code,
        game_name
    );

drop TABLE bi_dashboard.st_game_operate_channel_statistic ON CLUSTER cluster_group01;
CREATE TABLE bi_dashboard.st_game_operate_channel_statistic ON CLUSTER cluster_group01(
  `agg_dim` String COMMENT '聚合粒度',
  `date_time` Date COMMENT '时间',
  `category1` String COMMENT '一级业务类别',
  `category2` String COMMENT '二级业务类别',
  `category3` String COMMENT '三级业务类别',
  `customer_id` String COMMENT '合作方ID',
  `customer_name` String COMMENT '合作方名称',
  `cooperate_type` String COMMENT '合作方式',
  `agent_company_name` String COMMENT '媒体商公司名称',
  `agent_id` String COMMENT '媒体商',
  `channel_id` String COMMENT '子渠道',
  `cid_id` String COMMENT '活动策略',
  `billing_type` String COMMENT '计费方式',
  `game_category` UInt8 COMMENT '游戏类型',
  `game_category_name` String COMMENT '游戏类型名称',
  `game_code` String COMMENT '游戏code',
  `game_name` String COMMENT '游戏名称',
  `netbar_type` UInt8 COMMENT '网吧类型 1是渠道部网吧   2 非网吧 3是外部网吧',
  `source_tag` UInt8 COMMENT '数据来源 1是财务  2是业务',
  `reg_cnts` UInt32 COMMENT '注册',
  `reg_array` Array(String) COMMENT '注册明细',
  `newlogin_cnts` UInt32 COMMENT '新增',
  `newlogin_array` Array(String) COMMENT '新增明细',
  `active_cnts` UInt32 COMMENT '活跃',
  `active_array` Array(String) COMMENT '活跃明细',
  `newpay_users` UInt32 COMMENT '新增付费用户',
  `newpay_array` Array(String) COMMENT '新增付费用户明细',
  `newpay_money` Float32 COMMENT '新增付费金额',
  `pay_users` UInt32 COMMENT '付费用户数',
  `pay_array` Array(String) COMMENT '付费用户明细',
  `pay_cnts` UInt32 COMMENT '付费次数',
  `pay_money` Float32 COMMENT '付费金额',
  `drate1` Float32 COMMENT '次留',
  `LTV1` Float32 COMMENT 'LTV1',
  `LTV7` Float32 COMMENT 'LTV7',
  `LTV15` Float32 COMMENT 'LTV15',
  `LTV30` Float32 COMMENT 'LTV30',
  `arpu` Float32 COMMENT 'ARPU（活跃）',
  `arppu` Float32 COMMENT 'ARPPU（付费）',
  `real_income` Float32 COMMENT '实际收入',
  `real_cost` Float32 COMMENT '实际成本',
  `real_cp_cost` Float32 COMMENT '实际CP成本',
  `real_channel_cost` Float32 COMMENT '实际渠道成本',
  `real_other_cost` Float32 COMMENT '实际其他成本',
  `real_roi` Float32 COMMENT '实际ROI',
  `real_profit` Float32 COMMENT '实际利润',
  `estimate_income` Float32 COMMENT '预估收入',
  `estimate_rmb_income` Float32 COMMENT '预估人民币收入',
  `estimate_coupon_income` Float32 COMMENT '预估E币券收入',
  `estimate_cost` Float32 COMMENT '预估成本',
  `estimate_cp_cost` Float32 COMMENT '预估CP成本',
  `estimate_channel_cost` Float32 COMMENT '预估渠道成本',
  `estimate_coupon_cost` Float32 COMMENT '预估E币券成本',
  `timeline` String COMMENT '分区字段'
) ENGINE = ReplicatedMergeTree(
  '/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/bi_dashboard/st_game_operate_channel_statistic',
  '{replica}'
) PARTITION BY timeline
ORDER BY
  (
    agg_dim,
    date_time,
    category1,
    category2,
    category3,
    customer_id,
    customer_name,
    cooperate_type,
    agent_company_name,
    agent_id,
    channel_id,
    cid_id,
    billing_type,
    game_category,
    game_category_name,
    game_code,
    game_name,
    netbar_type,
    source_tag
  );