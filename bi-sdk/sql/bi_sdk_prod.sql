create database banyan_bi_sdk ON CLUSTER cluster_group01;

CREATE TABLE banyan_bi_sdk.sdk_app_web_local on cluster cluster_group01 ( `date` DateTime COMMENT '时间',
`product` UInt64 COMMENT '产品',
`source_mq` UInt8 COMMENT '0代表Sdk_App，1代表Sdk_Web',
`screen_width` String COMMENT '屏幕长',
`screen_height` String COMMENT '屏幕宽',
`channel` String COMMENT '渠道',
`browser` String COMMENT '浏览器',
`country` String COMMENT '国家',
`province` String COMMENT '省份',
`city` String COMMENT '城市',
`app_version` String COMMENT 'app版本',
`os` String COMMENT '系统',
`os_type` UInt8 COMMENT '系统类型(1:web;2:PC客户端;3:手机H5;4:微信页面；5：APP内嵌H5；6：ios，7：android)',
`os_version` String COMMENT '操作系统版本,如12.3.0',
`brand` String COMMENT '安装该APP的设备品牌，如APPLE,HUAWEI等',
`model` String COMMENT '安装该APP的设备型号,如iphone11等，',
`carrier` String COMMENT '运营商',
`user_type` UInt8 COMMENT '0代表注册，1代表访客',
`page_title` String COMMENT '页面title',
`page_url` String COMMENT '页面URL',
`pv` UInt32 COMMENT 'pv数量',
`startup` UInt32 COMMENT '启动次数（没有按照uid或cookie去重的统计）',
`uv` Array(String) COMMENT '去重的uuid 或者 cookie',
`new_uv` Array(String) COMMENT '新的去重的uuid 或者 cookie',
`active_user` Array(String) COMMENT '去重的uid',
`online_duration` UInt32 COMMENT '在线时长',
`ips` Array(String) COMMENT 'ip集合',
`start_sessions` Array(String) COMMENT '不去重的session',
`in_sessions` Array(String) COMMENT 'page_seq等于1的session',
`create_time` DateTime COMMENT '写入时间',
`timeline` Date COMMENT '分区字段',
`shard` UInt8 COMMENT '分片字段',
`insert_time` DateTime DEFAULT now()
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/sdk_app_web_local', '{replica}', create_time) PARTITION BY timeline
ORDER BY
( date,
product,
source_mq,
app_version,
screen_width,
screen_height,
os,
os_type,
os_version,
browser,
brand,
model,
user_type,
country,
province,
city,
channel,
carrier,
page_title,
page_url);

CREATE TABLE banyan_bi_sdk.sdk_app_web ON
CLUSTER cluster_group01 as banyan_bi_sdk.sdk_app_web_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
sdk_app_web_local,
shard);

CREATE TABLE banyan_bi_sdk.payment_info_local  on cluster cluster_group01 (
`date` DateTime COMMENT '时间',
 `os_type` UInt8 COMMENT '系统类型',
 `product` String COMMENT '产品',
 `channel` String COMMENT '渠道',
 `uid` String COMMENT '用户标识',
 `pay_fee` Float64 COMMENT '付费金额',
 `pay_frequency` UInt32 COMMENT '付费频率',
 `pay_order_ids` Array(String) COMMENT '付费成功订单',
 `create_time` DateTime COMMENT '写入时间',
 `timeline` Date COMMENT '分区字段',
 `shard` UInt8 COMMENT '分片字段',
 `os_version` String,
 `insert_time` DateTime DEFAULT now()
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/payment_info_local', '{replica}', create_time) PARTITION BY timeline ORDER BY (date, os_type, product, channel, uid);

CREATE TABLE banyan_bi_sdk.payment_info ON
CLUSTER cluster_group01 as banyan_bi_sdk.payment_info_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
payment_info_local,
shard);

CREATE TABLE banyan_bi_sdk.register_info_local on cluster cluster_group01 (
`date` DateTime COMMENT '时间',
 `os_type` UInt8 COMMENT '系统类型',
 `product` String COMMENT '产品',
 `channel` String COMMENT '渠道',
 `uid` String COMMENT '用户标识',
 `os_version` String COMMENT '系统版本',
 `timeline` Date COMMENT '分区字段',
 `shard` UInt8 COMMENT '分片字段',
 `create_time` DateTime COMMENT '写入时间',
 `insert_time` DateTime DEFAULT now()
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/register_info_local', '{replica}', create_time) PARTITION BY timeline ORDER BY (date, uid);


CREATE TABLE banyan_bi_sdk.register_info ON
CLUSTER cluster_group01 as banyan_bi_sdk.register_info_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
register_info_local,
shard);

CREATE TABLE banyan_bi_sdk.dm_bi_sdk_user_sum_d_local on
cluster cluster_group01 ( `agg_dim` String,
`dt` Date,
`product_id` String,
`channel_id` String,
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

CREATE TABLE banyan_bi_sdk.dm_bi_sdk_user_sum_d ON
CLUSTER cluster_group01 as banyan_bi_sdk.dm_bi_sdk_user_sum_d_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
dm_bi_sdk_user_sum_d_local,
product_id);

CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d_local on cluster cluster_group01 (
`agg_dim` String,
 `reg_date` Date,
 `product_id` String,
 `channel_id` String,
 `user_type` String,
 `period_type` String,
 `intervals` Int32,
 `initial_count` Int64,
 `retain_count` Int64,
 `pay_amount` Float32,
 `timeline` String
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/dws_bi_sdk_user_retain_d_local','{replica}') PARTITION BY timeline ORDER BY reg_date;

CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d ON
CLUSTER cluster_group01 as banyan_bi_sdk.dws_bi_sdk_user_retain_d_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
dws_bi_sdk_user_retain_d_local,
product_id);

CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m_local on cluster cluster_group01 (
`agg_dim` String,
 `reg_date` Date,
 `product_id` String,
 `channel_id` String,
 `user_type` String,
 `period_type` String,
 `intervals` Int32,
 `initial_count` Int64,
 `retain_count` Int64,
 `pay_amount` Float32,
 `timeline` String
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/dws_bi_sdk_user_retain_m_local','{replica}') PARTITION BY timeline ORDER BY reg_date;

CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m ON
CLUSTER cluster_group01 as banyan_bi_sdk.dws_bi_sdk_user_retain_m_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
dws_bi_sdk_user_retain_m_local,
product_id);

CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w_local on cluster cluster_group01 (
`agg_dim` String,
 `reg_date` Date,
 `product_id` String,
 `channel_id` String,
 `user_type` String,
 `period_type` String,
 `intervals` Int32,
 `initial_count` Int64,
 `retain_count` Int64,
 `pay_amount` Float32,
 `timeline` String
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/dws_bi_sdk_user_retain_w_local','{replica}') PARTITION BY timeline ORDER BY reg_date;

CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w ON
CLUSTER cluster_group01 as banyan_bi_sdk.dws_bi_sdk_user_retain_w_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
dws_bi_sdk_user_retain_w_local,
product_id);

CREATE TABLE banyan_bi_sdk.acc_reg_pay on cluster cluster_group01 (
`date` Date COMMENT '日期',
 `product` UInt64 COMMENT '产品线',
 `channel` String COMMENT '渠道',
 `os_type` UInt8 COMMENT '系统',
 `acc_reg_count` UInt32 COMMENT '累计注册用户数',
 `acc_pay_user_count` UInt32 COMMENT '累计付费用户数',
 `acc_pay_fee` Float32 COMMENT '累计付费金额'
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/acc_reg_pay','{replica}') PARTITION BY date ORDER BY (date, product, channel, os_type) ;

CREATE TABLE banyan_bi_sdk.acc_sdk_app_web on cluster cluster_group01 (
`date` Date COMMENT '日期',
 `product` UInt64 COMMENT '产品线',
 `channel` String COMMENT '渠道',
 `os_type` UInt8 COMMENT '页面部署的终端',
 `acc_active_user` UInt32 COMMENT '累计活跃用户数',
 `acc_visit_user` UInt32 COMMENT '累计访问用户数',
 `acc_visit_count` UInt32 COMMENT '累计访问次数'
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/acc_sdk_app_web','{replica}') PARTITION BY date ORDER BY (date, product, channel, os_type);

CREATE TABLE banyan_bi_sdk.product on cluster cluster_group01(
 `product_id` String,
 `product_name` String,
 `business` String,
 `business_type` String,
 `sdk_product_id` UInt64,
 `sdk_product_name` String,
 `is_valid` Int8,
 `shard` UInt8
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/product','{replica}') PRIMARY KEY product_id ORDER BY product_id;

CREATE TABLE banyan_bi_sdk.bi_channel_maintain on cluster cluster_group01(
  `source` String,
  `product_id` String,
  `billing_name` String,
  `cid` String,
  `agent_id` String,
  `agent_name` String,
  `channel_id` String,
  `channel_name` String,
  `remark` String
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/bi_channel_maintain','{replica}') ORDER BY (product_id,billing_name);

CREATE TABLE banyan_bi_sdk.sdk_error_local on cluster cluster_group01 (
`date` DateTime,
 `os_type` String,
 `product` UInt64,
 `channel` String,
 `crash` UInt32,
 `create_time` DateTime,
 `timeline` Date COMMENT '分区字段',
 `shard` UInt8 COMMENT '分片字段',
 `insert_time` DateTime DEFAULT now()
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/sdk_error_local', '{replica}', create_time) PARTITION BY timeline PRIMARY KEY date ORDER BY (date, os_type, product, channel);

CREATE TABLE banyan_bi_sdk.sdk_error ON
CLUSTER cluster_group01 as banyan_bi_sdk.sdk_error_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
sdk_error_local,
shard);

CREATE VIEW banyan_bi_sdk.view_acc_reg_pay on cluster cluster_group01 AS
SELECT
	acc_reg_pay.date,
	acc_reg_pay.product,
	acc_reg_pay.channel,
	acc_reg_pay.os_type,
	acc_reg_pay.acc_reg_count,
	acc_reg_pay.acc_pay_user_count,
	acc_reg_pay.acc_pay_fee
FROM
	banyan_bi_sdk.acc_reg_pay;

CREATE VIEW banyan_bi_sdk.view_acc_sdk_web on cluster cluster_group01 AS
SELECT
	acc_sdk_app_web.date,
	acc_sdk_app_web.product,
	acc_sdk_app_web.channel,
	acc_sdk_app_web.os_type,
	acc_sdk_app_web.acc_active_user,
	acc_sdk_app_web.acc_visit_count,
	acc_sdk_app_web.acc_visit_user
FROM
	banyan_bi_sdk.acc_sdk_app_web;

CREATE VIEW banyan_bi_sdk.view_acc_sdk_web_reg_pay on
cluster cluster_group01 AS
SELECT
	date,
	product,
	channel,
	os_type,
	view_acc_sdk_web.acc_active_user AS acc_active_user,
	view_acc_sdk_web.acc_visit_count AS acc_visit_count,
	view_acc_sdk_web.acc_visit_user AS acc_visit_user,
	view_acc_reg_pay.acc_pay_user_count AS acc_pay_user_count,
	view_acc_reg_pay.acc_reg_count AS acc_reg_count
FROM
	banyan_bi_sdk.view_acc_sdk_web
full join banyan_bi_sdk.view_acc_reg_pay
		using date,
	product,
	channel,
	os_type;

CREATE MATERIALIZED VIEW banyan_bi_sdk.view_register_info_local on cluster cluster_group01
ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/view_register_info_local', '{replica}', version) PARTITION BY product
ORDER BY
(product, uid) as
SELECT
	register_info_local.date AS date,
	register_info_local.os_type AS os_type,
	multiIf(register_info_local.os_type IN (1, 7), 'PC', register_info_local.os_type = 2, 'IOS', register_info_local.os_type = 3, 'Android', register_info_local.os_type = 4, 'WAP（手机H5）', register_info_local.os_type = 6, 'MiniProgram', 'other') AS os_name,
	assumeNotNull(product.product_id) AS product,
	if(isNull(bi_channel_maintain.agent_name)
	OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name) AS channel,
	assumeNotNull(register_info_local.uid) AS uid,
	register_info_local.create_time AS create_time,
	register_info_local.version as version,
	register_info_local.timeline AS timeline
FROM
	banyan_bi_sdk.register_info_local
LEFT JOIN banyan_bi_sdk.product ON
	product.business = register_info_local.product
LEFT JOIN banyan_bi_sdk.bi_channel_maintain ON
	(bi_channel_maintain.product_id = product.product_id)
	AND (bi_channel_maintain.billing_name = register_info_local.channel);

CREATE TABLE banyan_bi_sdk.view_register_info ON
CLUSTER cluster_group01 as banyan_bi_sdk.view_register_info_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
view_register_info_local);

CREATE VIEW banyan_bi_sdk.view_payment_info_local on cluster cluster_group01 AS
SELECT
	banyan_bi_sdk.payment_info_local.date AS date,
	banyan_bi_sdk.payment_info_local.os_type AS os_type,
	banyan_bi_sdk.product.product_id AS product,
	if(bi_channel_maintain.agent_name is null
	or bi_channel_maintain.agent_name = '', '其他', bi_channel_maintain.agent_name) AS channel,
	if(isNull(register_info_local_final.register_channel)
	OR (register_info_local_final.register_channel = ''), '其他', register_info_local_final.register_channel) AS register_channel,
	banyan_bi_sdk.payment_info_local.uid AS uid,
	banyan_bi_sdk.payment_info_local.pay_fee AS pay_fee,
	banyan_bi_sdk.payment_info_local.pay_frequency AS pay_frequency
FROM
	banyan_bi_sdk.payment_info_local FINAL
LEFT JOIN banyan_bi_sdk.product ON
	banyan_bi_sdk.product.business = banyan_bi_sdk.payment_info_local.product
LEFT JOIN banyan_bi_sdk.bi_channel_maintain on
	bi_channel_maintain.product_id = product.product_id
	and bi_channel_maintain.billing_name = payment_info_local.channel
LEFT JOIN (
	select
		uid, channel as register_channel
	from
		banyan_bi_sdk.view_register_info_local FINAL) as register_info_local_final ON
	register_info_local_final.uid = banyan_bi_sdk.payment_info_local.uid;

CREATE TABLE banyan_bi_sdk.view_payment_info ON
CLUSTER cluster_group01 as banyan_bi_sdk.view_payment_info_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
view_payment_info_local);

CREATE MATERIALIZED VIEW banyan_bi_sdk.view_payment_info_register_info_local on
cluster cluster_group01 ENGINE = ReplicatedReplacingMergeTree( '/clickhouse/tables/cluster_group01/{layer}-{sgroup01}/banyan_bi_sdk/view_payment_info_register_info_local', '{replica}', create_time ) PARTITION BY timeline
ORDER BY
( date, product, os_type, channel, uid, original_channel ) AS
SELECT
	assumeNotNull(payment_info_local.date) AS date,
	assumeNotNull(payment_info_local.os_type) AS os_type,
	multiIf( payment_info_local.os_type IN (1, 7), 'Web', os_type = 2, 'IOS', os_type = 3, 'Android', os_type = 4, 'H5', os_type = 6, '小程序', 'other' ) AS os_name,
	assumeNotNull(payment_info_local.product) AS product,
	assumeNotNull(payment_info_local.channel) AS original_channel,
	assumeNotNull(payment_info_local.uid) AS uid,
	assumeNotNull( if( isNull(register_info_local_final.channel)
	OR (register_info_local_final.channel = ''), '其他', register_info_local_final.channel ) ) AS channel,
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
		uid, date, assumeNotNull(product) AS product, if( isNull(bi_channel_maintain.agent_name)
		OR (bi_channel_maintain.agent_name = ''), '其他', bi_channel_maintain.agent_name ) AS channel, os_type
	FROM
		(
		SELECT
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

CREATE VIEW banyan_bi_sdk.view_register_info_yesterday on cluster cluster_group01 AS
SELECT
	date,
	product,
	channel,
	os_type,
	uid_array,
	yesterday_uid_array
FROM
	(
	SELECT
		toStartOfHour(view_register_info_local.date) AS date,
		product,
		channel,
		os_type,
		groupArray(uid) AS uid_array
	FROM
		banyan_bi_sdk.view_register_info_local final
	GROUP BY
		toStartOfHour(view_register_info_local.date),
		product,
		channel,
		os_type) AS _t1
FULL OUTER JOIN (
	SELECT
		addDays(toStartOfHour(view_register_info_local.date),
		1) AS date,
		product,
		channel,
		os_type,
		groupArray(uid) AS yesterday_uid_array
	FROM
		banyan_bi_sdk.view_register_info_local final
	where date <= now()
	GROUP BY
		toStartOfHour(view_register_info_local.date),
		product,
		channel,
		os_type) AS _t2
		USING (date,
	product,
	channel,
	os_type);

CREATE VIEW banyan_bi_sdk.view_sdk_app_error_local on cluster cluster_group01 AS
SELECT
	date,
	product,
	channel,
	length(arrayDistinct(start_sessions)) as startup,
	sdk_error_local.crash
FROM
	banyan_bi_sdk.sdk_app_web_local final
LEFT JOIN banyan_bi_sdk.sdk_error_local
		ON (sdk_app_web_local.date = sdk_error_local.date) AND (sdk_app_web_local.product = sdk_error_local.product) AND (sdk_app_web_local.channel = sdk_error_local.channel)
where sdk_app_web_local.source_mq = 0;

CREATE TABLE banyan_bi_sdk.view_sdk_app_error ON
CLUSTER cluster_group01 as banyan_bi_sdk.view_sdk_app_error_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
view_sdk_app_error_local);

CREATE table banyan_bi_sdk.sdk_app_sdk_web_local ON CLUSTER cluster_group01 (
`date` DateTime,
 `product` UInt64,
 `channel` String,
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
ORDER BY (date, product, channel, os_type, os, os_version, app_version, screen_width, screen_height, browser, country, province, city, brand, model, carrier);

CREATE TABLE banyan_bi_sdk.view_sdk_app_sdk_web ON
CLUSTER cluster_group01 as banyan_bi_sdk.sdk_app_sdk_web_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
sdk_app_sdk_web_local);

CREATE TABLE banyan_bi_sdk.sdk_app_sdk_web_page_local ON CLUSTER cluster_group01 (
`date` DateTime,
 `product` UInt64,
 `channel` String,
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
PARTITION BY timeline ORDER BY (date, product, channel, os_type, app_version, user_type, page_title, page_url);

CREATE TABLE banyan_bi_sdk.view_sdk_app_sdk_web_page ON
CLUSTER cluster_group01 as banyan_bi_sdk.sdk_app_sdk_web_page_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
sdk_app_sdk_web_page_local);

CREATE table banyan_bi_sdk.sdk_app_sdk_web_register_info_local ON CLUSTER cluster_group01 (
`date` DateTime,
 `product` UInt64,
 `channel` String,
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
PARTITION BY timeline ORDER BY (date, product, channel, os_type);

CREATE TABLE banyan_bi_sdk.view_sdk_app_sdk_web_register_info ON
CLUSTER cluster_group01 as banyan_bi_sdk.sdk_app_sdk_web_register_info_local ENGINE = Distributed(cluster_group01,
banyan_bi_sdk,
sdk_app_sdk_web_register_info_local);
