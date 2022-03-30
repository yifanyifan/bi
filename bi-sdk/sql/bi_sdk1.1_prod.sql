drop TABLE banyan_bi_sdk.bi_channel_maintain_transfer on cluster cluster_group01;
CREATE TABLE banyan_bi_sdk.bi_channel_maintain_transfer on cluster cluster_group01 (
`source` String,
 `product_id` String,
 `billing_id` String,
 `billing_name` String,
 `cid` String,
 `cid_name` String,
 `agent_id` String,
 `agent_name` String,
 `channel_id` String,
 `channel_name` String,
 `remark` String
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/bi_channel_maintain_transfer', '{replica}') ORDER BY (product_id, billing_id, billing_name);

drop TABLE banyan_bi_sdk.bi_channel_maintain on cluster cluster_group01;
CREATE TABLE banyan_bi_sdk.bi_channel_maintain on cluster cluster_group01 (
`source` String,
 `product_id` String,
 `billing_id` String,
 `billing_name` String,
 `cid` String,
 `cid_name` String,
 `agent_id` String,
 `agent_name` String,
 `channel_id` String,
 `channel_name` String,
 `remark` String
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/bi_channel_maintain', '{replica}') ORDER BY (product_id, billing_id, billing_name);

drop table banyan_bi_sdk.view_payment_info_register_info_local on cluster cluster_group01;
CREATE MATERIALIZED VIEW banyan_bi_sdk.view_payment_info_register_info_local on cluster cluster_group01
ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/view_payment_info_register_info_local', '{replica}', create_time)
PARTITION BY timeline
ORDER BY
( date, product, os_type, agent_name,channel_name,cid_name,billing_name, uid, original_channel ) AS
SELECT
	assumeNotNull(payment_info_local.date) AS date,
	assumeNotNull(payment_info_local.os_type) AS os_type,
	multiIf(os_type = 1, 'Web', os_type = 7, 'PC', os_type = 4, 'H5', os_type = 2, 'iOS', os_type = 3, 'Android', os_type = 6, '微信小程序', os_type = 8, '微信小游戏', os_type = 9, 'TV', 'other') AS os_name,
	assumeNotNull(payment_info_local.product) AS product,
	assumeNotNull(payment_info_local.channel) AS original_channel,
	assumeNotNull(payment_info_local.uid) AS uid,
	assumeNotNull(if(isNull(register_info_local_final.agent_name) OR (register_info_local_final.agent_name = ''), '其他', register_info_local_final.agent_name)) AS agent_name,
    assumeNotNull(if(isNull(register_info_local_final.channel_name) OR (register_info_local_final.channel_name = ''), '其他', register_info_local_final.channel_name)) AS channel_name,
    assumeNotNull(if(isNull(register_info_local_final.cid_name) OR (register_info_local_final.cid_name = ''), '其他', register_info_local_final.cid_name)) AS cid_name,
    assumeNotNull(if(isNull(register_info_local_final.billing_name) OR (register_info_local_final.billing_name = ''), '其他', register_info_local_final.billing_name)) AS billing_name,
	payment_info_local.pay_fee AS pay_fee,
	payment_info_local.pay_frequency AS pay_frequency,
	payment_info_local.pay_order_ids AS pay_order_ids,
	assumeNotNull(register_info_local_final.date) AS register_date,
	assumeNotNull(register_info_local_final.os_type) AS register_os_type,
	payment_info_local.create_time AS create_time,
	payment_info_local.timeline AS timeline
FROM
	banyan_bi_sdk.payment_info_local
LEFT JOIN (
	SELECT
		uid, date, assumeNotNull(product) AS product,
		if(isNull(bi_channel_maintain.agent_name) OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name) AS agent_name,
        if(isNull(bi_channel_maintain.channel_name) OR (bi_channel_maintain.channel_name = ''), '其他', bi_channel_maintain.channel_name) AS channel_name,
        if(isNull(bi_channel_maintain.cid_name) OR (bi_channel_maintain.cid_name = ''), '其他', bi_channel_maintain.cid_name) AS cid_name,
        if(isNull(bi_channel_maintain.billing_name) OR (bi_channel_maintain.billing_name = ''), '其他', bi_channel_maintain.billing_name) AS billing_name,
		os_type
	FROM
		(SELECT
			product, uid, argMax(date, version) AS date, argMax(channel, version) AS channel, argMax(os_type, version) AS os_type
		FROM
			banyan_bi_sdk.register_info_local
		WHERE
			uid IN (
			SELECT
				uid
			FROM
				banyan_bi_sdk.payment_info_local
			WHERE
				payment_info_local.date >= subtractDays(now(), 1) )
		GROUP BY
			product, uid ) AS register_info_local_final_temp
	LEFT JOIN banyan_bi_sdk.bi_channel_maintain ON
		( bi_channel_maintain.product_id = register_info_local_final_temp.product )
		AND ( bi_channel_maintain.billing_name = register_info_local_final_temp.channel ) ) AS register_info_local_final ON
	( payment_info_local.uid = register_info_local_final.uid )
	AND ( register_info_local_final.product = payment_info_local.product );

CREATE TABLE banyan_bi_sdk.view_payment_info_register_info ON
CLUSTER cluster_group01 as banyan_bi_sdk.view_payment_info_register_info_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
view_payment_info_register_info_local);

create view sdk_error_channel_maintain on cluster cluster_group01 AS
SELECT
	`date`,
	os_type,
	product,
	bi_channel_maintain.agent_name,
	bi_channel_maintain.channel_name,
	bi_channel_maintain.cid_name,
	bi_channel_maintain.billing_name,
	crash,
	create_time,
	timeline,
	shard,
	insert_time
FROM
	banyan_bi_sdk.sdk_error_local
left join banyan_bi_sdk.bi_channel_maintain on
	sdk_error_local.product = bi_channel_maintain.product_id
	and sdk_error_local.channel = bi_channel_maintain.billing_id;

drop TABLE banyan_bi_sdk.sdk_error ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.sdk_error ON CLUSTER cluster_group01 as
banyan_bi_sdk.sdk_error_channel_maintain ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
sdk_error_channel_maintain,
shard);


drop table banyan_bi_sdk.view_sdk_app_error_local on cluster cluster_group01;
CREATE VIEW banyan_bi_sdk.view_sdk_app_error_local on cluster cluster_group01 AS
SELECT
	_t1.date,
	_t1.product,
	bi_channel_maintain.agent_name,
	bi_channel_maintain.channel_name,
	bi_channel_maintain.cid_name,
	bi_channel_maintain.billing_name,
	_t1.startup,
	_t1.crash
from
	(
	SELECT
		date, product, channel, length(arrayDistinct(start_sessions)) AS startup, sdk_error_local.crash
	FROM
		banyan_bi_sdk.sdk_app_web_local FINAL
	LEFT JOIN banyan_bi_sdk.sdk_error_local
			using date, product, channel
	WHERE
		sdk_app_web_local.source_mq = 0) as _t1
left join banyan_bi_sdk.bi_channel_maintain on
	bi_channel_maintain.product_id = _t1.product
	and bi_channel_maintain.billing_id = _t1.channel;

drop table banyan_bi_sdk.view_sdk_app_error ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.view_sdk_app_error ON
CLUSTER cluster_group01 as banyan_bi_sdk.view_sdk_app_error_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
view_sdk_app_error_local);

drop table banyan_bi_sdk.sdk_app_sdk_web_local ON CLUSTER cluster_group01;
CREATE table banyan_bi_sdk.sdk_app_sdk_web_local ON CLUSTER cluster_group01 (
`date` DateTime,
 `product` String,
  agent_name String comment '媒体商',
  channel_name String comment '子渠道',
  cid_name String comment '活动策略',
  billing_name String comment '计费名',
 `os_type` UInt8,
 `os_name` String,
 `os` String,
 `os_version` String,
 `app_version` String,
 `os_concat_version` String,
 `screen_width` String,
 `screen_height` String,
 `resolution` String,
 `browser` String,
 `country` String,
 `province` String,
 `city` String,
 `brand` String,
 `model` String,
 `carrier` String,
 `create_time` DateTime,
 `timeline` Date,
 `pv` UInt64,
 `start_sessions` Array(String),
 `uv` Array(String),
 `new_uv` Array(String),
 `active_user` Array(String),
 `online_duration` UInt64,
 `ips` Array(String)
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/sdk_app_sdk_web_local', '{replica}', create_time)
PARTITION BY timeline
ORDER BY (date, product, agent_name, channel_name, cid_name, billing_name, os_type, os, os_version, app_version, screen_width, screen_height, browser, country, province, city, brand, model, carrier);

drop  TABLE banyan_bi_sdk.view_sdk_app_sdk_web ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.view_sdk_app_sdk_web ON
CLUSTER cluster_group01 as banyan_bi_sdk.sdk_app_sdk_web_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
sdk_app_sdk_web_local);

drop table banyan_bi_sdk.sdk_app_sdk_web_page_local ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.sdk_app_sdk_web_page_local ON CLUSTER cluster_group01 (
`date` DateTime,
 `product` String,
 agent_name String comment '媒体商',
 channel_name String comment '子渠道',
 cid_name String comment '活动策略',
 billing_name String comment '计费名',
 `os_type` UInt8,
 `user_type` UInt8,
 `app_version` String,
 `page_title` String,
 `page_url` String,
 `create_time` DateTime,
 `timeline` Date,
 `pv` UInt64,
 `in_sessions` Array(String),
 `start_sessions` Array(String),
 `uv` Array(String),
 `active_user` Array(String),
 `online_duration` UInt64,
 `online_duration_m` Float64,
 `ips` Array(String)
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/sdk_app_sdk_web_page_local', '{replica}', create_time)
PARTITION BY timeline ORDER BY (date, product, agent_name, channel_name, cid_name, billing_name, os_type, app_version, user_type, page_title, page_url);

drop TABLE banyan_bi_sdk.view_sdk_app_sdk_web_page ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.view_sdk_app_sdk_web_page ON
CLUSTER cluster_group01 as banyan_bi_sdk.sdk_app_sdk_web_page_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
sdk_app_sdk_web_page_local);

drop table banyan_bi_sdk.sdk_app_sdk_web_register_info_local ON CLUSTER cluster_group01;
CREATE table banyan_bi_sdk.sdk_app_sdk_web_register_info_local ON CLUSTER cluster_group01 (
`date` DateTime,
 `product` String,
  agent_name String comment '媒体商',
  channel_name String comment '子渠道',
  cid_name String comment '活动策略',
  billing_name String comment '计费名',
 `os_type` UInt8,
 `os_name` String,
 `create_time` DateTime,
 `timeline` Date,
 `pv` UInt64,
 `start_sessions` Array(String),
 `uv` Array(String),
 `new_uv` Array(String),
 `active_user` Array(String),
 `online_duration` UInt64,
 `register_user` Array(String),
 `yesterday_register_user` Array(String),
 `yesterday_register_today_active_user` Array(String),
 `pay_user` Array(String),
 `pay_fee_sum` Float64,
 `pay_user_fee` Array(Tuple(String,
 Float64)),
 `pay_frequency` UInt64,
 `original_channel_pay_user` Array(String),
 `original_channel_pay_fee_sum` Float64,
 `original_channel_pay_frequency` UInt64
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/sdk_app_sdk_web_register_info_local', '{replica}', create_time)
PARTITION BY timeline ORDER BY (date, product, agent_name, channel_name, cid_name, billing_name, os_type);

drop TABLE banyan_bi_sdk.view_sdk_app_sdk_web_register_info ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.view_sdk_app_sdk_web_register_info ON
CLUSTER cluster_group01 as banyan_bi_sdk.sdk_app_sdk_web_register_info_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
sdk_app_sdk_web_register_info_local);

drop TABLE banyan_bi_sdk.acc_reg_pay ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.acc_reg_pay ON CLUSTER cluster_group01(
`date` Date COMMENT '日期',
 `agg_dim` String,
 `product` String COMMENT '产品线',
 `agent_name` String COMMENT '媒体商',
 `channel_name` String COMMENT '子渠道',
 `cid_name` String COMMENT '活动策略',
 `billing_name` String COMMENT '计费名',
 `acc_reg_count` UInt32 COMMENT '累计注册用户数',
 `acc_pay_user_count` UInt32 COMMENT '累计付费用户数',
 `acc_pay_fee` Float32 COMMENT '累计付费金额'
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/acc_reg_pay', '{replica}') PARTITION BY date ORDER BY (date, agg_dim, product, agent_name, channel_name, cid_name, billing_name);

drop TABLE banyan_bi_sdk.acc_sdk_app_web ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.acc_sdk_app_web ON CLUSTER cluster_group01(
`date` Date COMMENT '日期',
 `agg_dim` String,
 `product` String COMMENT '产品线',
 `agent_name` String COMMENT '媒体商',
 `channel_name` String COMMENT '子渠道',
 `cid_name` String COMMENT '活动策略',
 `billing_name` String COMMENT '计费名',
 `acc_active_user` UInt32 COMMENT '累计活跃用户数',
 `acc_visit_user` UInt32 COMMENT '累计访问用户数',
 `acc_visit_count` UInt32 COMMENT '累计访问次数'
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/acc_sdk_app_web', '{replica}') PARTITION BY date ORDER BY (date, agg_dim, product, agent_name, channel_name, cid_name, billing_name);



drop TABLE banyan_bi_sdk.view_acc_reg_pay ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.view_acc_reg_pay ON
CLUSTER cluster_group01 as banyan_bi_sdk.acc_reg_pay ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
acc_reg_pay);

drop TABLE banyan_bi_sdk.view_acc_sdk_web ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.view_acc_sdk_web ON
CLUSTER cluster_group01 as banyan_bi_sdk.acc_sdk_app_web ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
acc_sdk_app_web);

drop table banyan_bi_sdk.view_acc_sdk_web_reg_pay ON CLUSTER cluster_group01;
CREATE VIEW banyan_bi_sdk.view_acc_sdk_web_reg_pay ON CLUSTER cluster_group01(
`date` Date,
 agg_dim String,
 `product` String,
 `agent_name` String,
 `channel_name` String,
 `cid_name` String,
 `billing_name` String,
 `acc_active_user` UInt32,
 `acc_visit_count` UInt32,
 `acc_visit_user` UInt32,
 `acc_pay_user_count` UInt32,
 `acc_reg_count` UInt32) AS SELECT date,agg_dim,
 product,
 agent_name,
 channel_name,
 cid_name,
 billing_name,
 view_acc_sdk_web.acc_active_user AS acc_active_user,
 view_acc_sdk_web.acc_visit_count AS acc_visit_count,
 view_acc_sdk_web.acc_visit_user AS acc_visit_user,
 view_acc_reg_pay.acc_pay_user_count AS acc_pay_user_count,
 view_acc_reg_pay.acc_reg_count AS acc_reg_count FROM banyan_bi_sdk.view_acc_sdk_web FULL OUTER JOIN banyan_bi_sdk.view_acc_reg_pay USING (date,agg_dim,
 product,
 agent_name,
 channel_name,
 cid_name,
 billing_name);

drop TABLE banyan_bi_sdk.dm_bi_sdk_user_sum_d_local on cluster cluster_group01;
CREATE TABLE banyan_bi_sdk.dm_bi_sdk_user_sum_d_local on
cluster cluster_group01 (
`agg_dim` String,
`dt` Date,
`product_id` String,
agent_name String,
channel_name String,
cid_name String,
billing_name String,
`all_users_cnt` Int64,
`users_introducing` Int64,
`users_growing` Int64,
`users_slient` Int64,
`users_churn` Int64,
`users_introducing_d` Int64,
`users_growing_d` Int64,
`users_slient_d` Int64,
`users_churn_d` Int64,
`churn_rate` Float32,
`dau_rate` Float32,
`wau_rate` Float32,
`mau_rate` Float32,
`l7dau_rate` Float32,
`l30dau_rate` Float32,
`dau` Int64,
`l7dau` Int64,
`l30dau` Int64,
`wau` Int64,
`mau` Int64,
`reg_days` Int64,
`reguser_cnt` Int64,
`active_days_30d` Int64,
`activeuser_cnt_30d` Int64,
`timeline` String ) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/dm_bi_sdk_user_sum_d_local','{replica}') PARTITION BY timeline
ORDER BY
dt;

drop  TABLE banyan_bi_sdk.dm_bi_sdk_user_sum_d ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.dm_bi_sdk_user_sum_d ON
CLUSTER cluster_group01 as banyan_bi_sdk.dm_bi_sdk_user_sum_d_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
dm_bi_sdk_user_sum_d_local);

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d_local on cluster cluster_group01;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d_local on cluster cluster_group01 (
`agg_dim` String,
 `reg_date` Date,
 `product_id` String,
agent_name String,
channel_name String,
cid_name String,
billing_name String,
 `user_type` String,
 `period_type` String,
 `intervals` Int32,
 `initial_count` Int64,
 `retain_count` Int64,
 `pay_amount` Float32,
 `timeline` String
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/dws_bi_sdk_user_retain_d_local','{replica}') PARTITION BY timeline ORDER BY reg_date;

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d ON
CLUSTER cluster_group01 as banyan_bi_sdk.dws_bi_sdk_user_retain_d_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
dws_bi_sdk_user_retain_d_local);

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m_local on cluster cluster_group01;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m_local on cluster cluster_group01 (
`agg_dim` String,
 `reg_date` Date,
 `product_id` String,
 agent_name String,
 channel_name String,
 cid_name String,
 billing_name String,
 `user_type` String,
 `period_type` String,
 `intervals` Int32,
 `initial_count` Int64,
 `retain_count` Int64,
 `pay_amount` Float32,
 `timeline` String
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/dws_bi_sdk_user_retain_m_local','{replica}') PARTITION BY timeline ORDER BY reg_date;

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m ON
CLUSTER cluster_group01 as banyan_bi_sdk.dws_bi_sdk_user_retain_m_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
dws_bi_sdk_user_retain_m_local);

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w_local on cluster cluster_group01;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w_local on cluster cluster_group01 (
`agg_dim` String,
 `reg_date` Date,
 `product_id` String,
 agent_name String,
 channel_name String,
 cid_name String,
 billing_name String,
 `user_type` String,
 `period_type` String,
 `intervals` Int32,
 `initial_count` Int64,
 `retain_count` Int64,
 `pay_amount` Float32,
 `timeline` String
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/dws_bi_sdk_user_retain_w_local','{replica}') PARTITION BY timeline ORDER BY reg_date;

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w ON CLUSTER cluster_group01;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w ON
CLUSTER cluster_group01 as banyan_bi_sdk.dws_bi_sdk_user_retain_w_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
dws_bi_sdk_user_retain_w_local);