drop TABLE bi_dashboard.st_game_operate_kpi_statistic ON CLUSTER cluster_group01;
CREATE TABLE bi_dashboard.st_game_operate_kpi_statistic ON CLUSTER cluster_group01 (
 `date_time` Date COMMENT '时间',
  new_user UInt32 COMMENT '日均新增用户量',
  pay_user UInt32 COMMENT '付费用户数',
  ka_product UInt32 COMMENT 'KA产品数量',
  cps_channel UInt32 COMMENT 'CPS的KA渠道数量',
  no_cps_channel UInt32 COMMENT '非CPS的KA渠道数量',
  keral_user UInt32 COMMENT '总充值超过2w的用户数',
  update_time DateTime COMMENT '更新时间',
  `timeline` String  COMMENT '分区字段'
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/bi_dashboard/st_game_operate_kpi_statistic', '{replica}')
PARTITION BY timeline ORDER BY date_time;

drop TABLE bi_dashboard.st_game_operate_roi_statistic ON CLUSTER cluster_group01;
CREATE TABLE bi_dashboard.st_game_operate_roi_statistic ON CLUSTER cluster_group01 (
 `date_time` Date COMMENT '时间',
 `kpi_id` UInt32 COMMENT '关联st_game_operate_kpi_statistic.id',
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
 `agent_id` String COMMENT '媒体商',
 `channel_id` String COMMENT '子渠道',
 `game_category` UInt8 COMMENT '游戏类型',
 `game_category_name` String COMMENT '游戏类型名称',
 `game_code` String COMMENT '游戏code',
 `game_name` String COMMENT '游戏名称',
 `real_income` Float32 COMMENT '实际收入',
 `real_cost` Float32 COMMENT '实际成本',
 `real_roi` Float32 COMMENT '实际ROI',
 `estimate_income` Float32 COMMENT '预估收入',
 `estimate_cost` Float32 COMMENT '预估成本',
 `estimate_roi` Float32 COMMENT '预估ROI',
 `timeline` String  COMMENT '分区字段'
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/bi_dashboard/st_game_operate_roi_statistic', '{replica}')
PARTITION BY timeline ORDER BY (date_time,kpi_id,group_name,category1,category2,category3,income_target_s,income_target_a,income_target_b,income_target_c,income_target_d,profit_target_s,profit_target_a,profit_target_b,profit_target_c,profit_target_d,agent_id,channel_id,game_code,game_name);

ALTER TABLE bi_dashboard.st_game_operate_channel_statistic ADD column estimate_other_cost Float32 COMMENT '预估其他成本' after estimate_channel_cost;
ALTER TABLE bi_dashboard.st_game_operate_channel_statistic ADD column real_other_cost Float32 COMMENT '实际其他成本' after real_cost;
ALTER TABLE bi_dashboard.st_game_operate_channel_statistic ADD column real_channel_cost Float32 COMMENT '实际渠道成本' after real_cost;
ALTER TABLE bi_dashboard.st_game_operate_channel_statistic ADD column real_cp_cost Float32 COMMENT '实际CP成本' after real_cost;


ALTER TABLE bi_dashboard.st_game_operate_pruduct_quality_statistic ADD column rmb_income Float32 COMMENT 'RMB充值' after arppu;
ALTER TABLE bi_dashboard.st_game_operate_pruduct_quality_statistic ADD column estimate_other_cost Float32 COMMENT '预估其他成本' after estimate_channel_cost;
ALTER TABLE bi_dashboard.st_game_operate_pruduct_quality_statistic ADD column real_other_cost Float32 COMMENT '实际其他成本' after real_channel_cost;