-- flink写入，注册、付费、行为关联起来的聚合表。
CREATE TABLE banyan_bi_sdk.sdk_app_web_register_payment_agg_local on cluster cluster_bidev(
    `date` DateTime COMMENT '时间',
    `product_id` String COMMENT '产品',
    `channel_id` String COMMENT '渠道',
    `agent_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'agent_name', tuple(product_id,upper(channel_id)), '未知'),
    `channel_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'channel_name', tuple(product_id,upper(channel_id)), '未知'),
    `sub_channel_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'sub_channel_name', tuple(product_id,upper(channel_id)), '未知'),
    `billing_name` String ALIAS concat(dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'pid_alias', tuple(product_id,upper(channel_id)), '未知'), '(', upper(channel_id), ')'),
    `os_type` Int8 COMMENT '系统类型',
    `os_name` String COMMENT '系统名称',
    `app_version` String COMMENT '版本',
    `data_type` Int8 COMMENT '数据类型。1：行为；2：活跃；3：注册；4：付费。',
    `pv` Int64 COMMENT 'pv',
    `session` Array(String) COMMENT '会话。跳出率：(session与session_again的差集)/session',
    `session_again` Array(String) COMMENT 'page_sequence大于1的会话',
    `online_duration` Int64 COMMENT '页面停留时长',
    `uv` Array(String) COMMENT 'uuid或cookie。新游客：uv_first与uv_with_uid的差集；老游客：uv与uv_with_uid的差集，再求与uv_first的差集',
    `uv_first` Array(String) COMMENT '首次出现的uuid或cookie',
    `uv_with_uid` Array(String) COMMENT '可以关联上uid的uuid或cookie，表示角色已经变成用户',
    `uid` Array(String) COMMENT '活跃用户id。新注册：uid与register_user交集；老用户：uid与register_user差集',
    `register_user` Array(String) COMMENT '注册用户',
    `pay_user` Array(String) COMMENT '付费用户',
    `pay_user_register_today` Array(String) COMMENT '今日注册的付费用户',
    `pay_user_register_before` Array(String) COMMENT '往日注册的付费用户',
    `pay_user_first` Array(String) COMMENT '首次付费用户',
    `pay_user_again` Array(String) COMMENT '复购用户',    
    `pay_fee` Array(Tuple(String,Float64)) COMMENT '付费金额',
    `pay_fee_register_today` Array(Tuple(String,Float64)) COMMENT '今日注册用户的付费金额',
    `pay_fee_register_before` Array(Tuple(String,Float64)) COMMENT '往日注册用户的付费金额',
    `pay_fee_register_yesterday` Array(Tuple(String,Float64)) COMMENT '昨日注册用户的付费金额。用于计算LTV1',
    `pay_fee_first` Array(Tuple(String,Float64)) COMMENT '首次付费金额',
    `pay_fee_again` Array(Tuple(String,Float64)) COMMENT '复购金额',
    `pay_fee_first_day` Array(Tuple(String,Float64)) COMMENT '首次付费用户当日付费金额',
    `success_order_id` Array(String) COMMENT '付费成功订单',
    `order_id` Array(String) COMMENT '订单',
    `version` Int64 COMMENT '版本',
    `insert_time` DateTime DEFAULT now()
) ENGINE = ReplicatedReplacingMergeTree(
    '/clickhouse/tables/cluster_bidev/{layer}-{sgroup01}/banyan_bi_sdk/sdk_app_web_register_payment_agg_local',
    '{replica}',
    version
) PARTITION BY toDate(date)
ORDER BY
    (
        date,
        product_id,
        channel_id,
        os_type,
        os_name,
        app_version,
        data_type
    ); 

DROP TABLE IF EXISTS banyan_bi_sdk.sdk_app_web_register_payment_agg_hour_materialized_view_0 ON CLUSTER cluster_bidev sync;    
CREATE MATERIALIZED VIEW banyan_bi_sdk.sdk_app_web_register_payment_agg_hour_materialized_view_0 ON CLUSTER cluster_bidev 
to banyan_bi_sdk.sdk_app_web_register_payment_agg_hour  
AS 
SELECT
    date,
    product_id,
    channel_id,
    os_type,
    os_name,
    app_version,
    maxMap(([xxHash32(concat(toString(date),product_id,channel_id,toString(os_type),app_version))],[pv])) as pv,
    uniqExactArrayState(session) as session_uniq,
    groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), session))) as session_bit,
    uniqExactArrayState(session_again) as session_again_uniq,
    groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), session_again))) as session_again_bit,
    maxMap(([xxHash32(concat(toString(date),product_id,channel_id,toString(os_type),app_version))],[online_duration])) as online_duration,
	uniqExactArrayState(uv) as uv_uniq,
    groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), uv))) as uv_bit,
	uniqExactArrayState(uv_first) as uv_first_uniq,
    groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), uv_first))) as uv_first_bit,
	uniqExactArrayState(uv_with_uid) as uv_with_uid_uniq,
    groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), uv_with_uid))) as uv_with_uid_bit,
	uniqExactArrayState(uid) as uid_uniq,
    groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), uid))) as uid_bit,
	uniqExactArrayState(register_user) as register_user_uniq,
    groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), register_user))) as register_user_bit,
	uniqExactArrayState(pay_user) as pay_user_uniq,
	uniqExactArrayState(pay_user_register_today) as pay_user_register_today_uniq,
	uniqExactArrayState(pay_user_register_before) as pay_user_register_before_uniq,
	uniqExactArrayState(pay_user_first) as pay_user_first_uniq,
	uniqExactArrayState(pay_user_again) as pay_user_again_uniq,
	maxMap((arrayMap(x->xxHash32(x.1), pay_fee),arrayMap(x->x.2, pay_fee))) as pay_fee,
    maxMap((arrayMap(x->xxHash32(x.1), pay_fee_register_today),arrayMap(x->x.2, pay_fee_register_today))) as pay_fee_register_today,
    maxMap((arrayMap(x->xxHash32(x.1), pay_fee_register_before),arrayMap(x->x.2, pay_fee_register_before))) as pay_fee_register_before,
    maxMap((arrayMap(x->xxHash32(x.1), pay_fee_register_yesterday),arrayMap(x->x.2, pay_fee_register_yesterday))) as pay_fee_register_yesterday,
    maxMap((arrayMap(x->xxHash32(x.1), pay_fee_first),arrayMap(x->x.2, pay_fee_first))) as pay_fee_first,
    maxMap((arrayMap(x->xxHash32(x.1), pay_fee_again),arrayMap(x->x.2, pay_fee_again))) as pay_fee_again,
    maxMap((arrayMap(x->xxHash32(x.1), pay_fee_first_day),arrayMap(x->x.2, pay_fee_first_day))) as pay_fee_first_day,
	uniqExactArrayState(success_order_id) as success_order_id_uniq,
	uniqExactArrayState(order_id) as order_id_uniq
FROM banyan_bi_sdk.sdk_app_web_register_payment_agg_local
GROUP BY date,
        product_id,
        channel_id,
        os_type,
        os_name,
        app_version;

DROP TABLE IF EXISTS banyan_bi_sdk.sdk_app_web_register_payment_agg_hour_materialized_view_1 ON CLUSTER cluster_bidev sync;
CREATE MATERIALIZED VIEW banyan_bi_sdk.sdk_app_web_register_payment_agg_hour_materialized_view_1 ON CLUSTER cluster_bidev 
to banyan_bi_sdk.sdk_app_web_register_payment_agg_hour  
AS 
SELECT
    addDays(_t1.date, 1) as date,
    product_id,
    channel_id,
    os_type,
    os_name,
    app_version,
    uniqExactArrayState(uid) as yesterday_uid_uniq,
    uniqExactArrayState(register_user) as yesterday_register_user_uniq,
    groupBitmapOrState(bitmapBuild(arrayMap(x->xxHash32(x), register_user))) as yesterday_register_user_bit,
    maxMap((arrayMap(x->xxHash32(x.1), pay_fee_register_today),arrayMap(x->x.2, pay_fee_register_today))) as yesterday_pay_fee_register_today
FROM
    banyan_bi_sdk.sdk_app_web_register_payment_agg_local as _t1
GROUP BY
    _t1.date,
    product_id,
    channel_id,
    os_type,
    os_name,
    app_version;            

DROP TABLE IF EXISTS banyan_bi_sdk.sdk_app_web_register_payment_agg_hour_materialized_view_2 ON CLUSTER cluster_bidev sync;
CREATE MATERIALIZED VIEW banyan_bi_sdk.sdk_app_web_register_payment_agg_hour_materialized_view_2 ON CLUSTER cluster_bidev 
to banyan_bi_sdk.sdk_app_web_register_payment_agg_hour  
AS 
SELECT 
	addDays(_t1.date, _t2.number) as date,
	product_id,
    channel_id,
    os_type,
    os_name,
    app_version,
	uid as last_thirty_days_uid_uniq
from
(SELECT
	date,
	product_id,
    channel_id,
    os_type,
    os_name,
    app_version,
	uniqArrayState(uid) as uid
from
	banyan_bi_sdk.sdk_app_web_register_payment_agg_local
group by
	date,
	product_id,
    channel_id,
    os_type,
    os_name,
    app_version) as _t1,numbers(1,30) as _t2;

-- flink写入，注册留存。
DROP TABLE IF EXISTS banyan_bi_sdk.user_retain_local on cluster cluster_bidev sync;
CREATE TABLE banyan_bi_sdk.user_retain_local on cluster cluster_bidev(
    `date` DateTime COMMENT '起始时间',
    `product_id` String COMMENT '产品',
    `channel_id` String COMMENT '渠道',
    `agent_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'agent_name', tuple(product_id,upper(channel_id)), '未知'),
    `channel_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'channel_name', tuple(product_id,upper(channel_id)), '未知'),
    `sub_channel_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'sub_channel_name', tuple(product_id,upper(channel_id)), '未知'),
    `billing_name` String ALIAS concat(dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'pid_alias', tuple(product_id,upper(channel_id)), '未知'), '(', upper(channel_id), ')'),
    `os_type` Int8 COMMENT '系统类型',
    `os_name` String COMMENT '系统名称',
    `interval` Int32 COMMENT '距起始时间间隔',
    `interval_date` DateTime COMMENT '间隔时间',
    `data_type` Int8 COMMENT '数据类型。2：活跃；3：注册；4：付费。',
    `initial` Array(String) COMMENT '起始度量(注册用户)',
    `retain` Array(String) COMMENT '留存度量(活跃用户)',
    `pay_user` Array(String) COMMENT '付费用户',
    `pay_fee` Array(Tuple(String,Float64)) COMMENT '付费金额',
    `version` Int64 COMMENT '创建时间',
    `insert_time` DateTime DEFAULT now()
) ENGINE = ReplicatedReplacingMergeTree (
    '/clickhouse/tables/cluster_bidev/{layer}-{sgroup01}/banyan_bi_sdk/user_retain_local',
    '{replica}',
    version
) PARTITION BY toDate(date)
ORDER BY
    (
        date,
        product_id,
        channel_id,
        os_type,
        os_name,
        `interval`,
        interval_date,
        data_type
    );    

-- flink写入，付费留存
DROP TABLE IF EXISTS banyan_bi_sdk.payment_retain_local on cluster cluster_bidev sync;
CREATE TABLE banyan_bi_sdk.payment_retain_local on cluster cluster_bidev(
    `date` DateTime COMMENT '起始时间',
    `product_id` String COMMENT '产品',
    `channel_id` String COMMENT '渠道',
    `agent_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'agent_name', tuple(product_id,upper(channel_id)), '未知'),
    `channel_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'channel_name', tuple(product_id,upper(channel_id)), '未知'),
    `sub_channel_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'sub_channel_name', tuple(product_id,upper(channel_id)), '未知'),
    `billing_name` String ALIAS concat(dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'pid_alias', tuple(product_id,upper(channel_id)), '未知'), '(', upper(channel_id), ')'),
    `os_type` Int8 COMMENT '系统类型',
    `os_name` String COMMENT '系统名称',
    `interval` Int32 COMMENT '距起始时间间隔',
    `interval_date` DateTime COMMENT '间隔时间',
    `data_type` Int8 COMMENT '数据类型。2：活跃；4：付费。',
    `initial` Array(String) COMMENT '起始度量(付费用户)',
    `retain` Array(String) COMMENT '留存度量(活跃用户)',
    `version` Int64 COMMENT '创建时间',
    `insert_time` DateTime DEFAULT now()
) ENGINE = ReplicatedReplacingMergeTree (
    '/clickhouse/tables/cluster_bidev/{layer}-{sgroup01}/banyan_bi_sdk/payment_retain_local',
    '{replica}',
    version
) PARTITION BY toDate(date)
ORDER BY
    (
        date,
        product_id,
        channel_id,
        os_type,
        os_name,
        `interval`,
        interval_date,
        data_type
    );    


-- flink写入，首付LTV
DROP TABLE IF EXISTS banyan_bi_sdk.payment_first_ltv_local on cluster cluster_bidev sync;
CREATE TABLE banyan_bi_sdk.payment_first_ltv_local on cluster cluster_bidev(
    `date` DateTime COMMENT '起始时间',
    `product_id` String COMMENT '产品',
    `channel_id` String COMMENT '渠道',
    `agent_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'agent_name', tuple(product_id,upper(channel_id)), '未知'),
    `channel_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'channel_name', tuple(product_id,upper(channel_id)), '未知'),
    `sub_channel_name` String ALIAS dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'sub_channel_name', tuple(product_id,upper(channel_id)), '未知'),
    `billing_name` String ALIAS concat(dictGetStringOrDefault('banyan_bi_sdk.dict_mysql_dm_product_channel_dim', 'pid_alias', tuple(product_id,upper(channel_id)), '未知'), '(', upper(channel_id), ')'),
    `os_type` Int8 COMMENT '系统类型',
    `os_name` String COMMENT '系统名称',
    `interval` Int32 COMMENT '距起始时间间隔',
    `interval_date` DateTime COMMENT '间隔时间',
    `data_type` Int8 COMMENT '数据类型。4：付费金额；5：首次付费用户。',
    `initial` Array(String) COMMENT '起始度量(首付用户)',
    `pay_fee` Array(Tuple(String,Float64)) COMMENT '付费金额',
    `version` Int64 COMMENT '创建时间',
    `insert_time` DateTime DEFAULT now()
) ENGINE = ReplicatedReplacingMergeTree (
    '/clickhouse/tables/cluster_bidev/{layer}-{sgroup01}/banyan_bi_sdk/payment_first_ltv_local',
    '{replica}',
    version
) PARTITION BY toDate(date)
ORDER BY
    (
        date,
        product_id,
        channel_id,
        os_type,
        os_name,
        `interval`,
        interval_date,
        data_type
    );    

drop TABLE IF EXISTS banyan_bi_sdk.user_retain_day_materialized_view_pay_fee ON CLUSTER cluster_bidev sync;
CREATE MATERIALIZED VIEW banyan_bi_sdk.user_retain_day_materialized_view_pay_fee ON CLUSTER cluster_bidev 
to banyan_bi_sdk.user_retain_day_materialized_view      
AS 
select 
    `date`,
    product_id,
	channel_id,
	os_type,
	os_name,
	`number` as `interval`,
    addDays(`interval_date`, number) as `interval_date`,
    `pay_fee`
from 
(SELECT
	toDate(_t1.`date`) as `date`,
	product_id,
	channel_id,
	os_type,
	os_name,
	`interval`,
    toDate(_t1.`interval_date`) as `interval_date`,
    maxMap((arrayMap(x->xxHash32(x.1), pay_fee),arrayMap(x->x.2, pay_fee))) as pay_fee
FROM
	banyan_bi_sdk.user_retain_local as _t1	 
group by
	toDate(_t1.`date`),
	product_id,
	channel_id,
	os_type,
	os_name,
	`interval`,
    toDate(_t1.`interval_date`)
) as _tt1,numbers(0,370) as _tt2 where _tt2.number >= _tt1.`interval`;    


drop TABLE IF EXISTS banyan_bi_sdk.payment_retain_day_materialized_view_retain ON CLUSTER cluster_bidev sync;
CREATE MATERIALIZED VIEW banyan_bi_sdk.payment_retain_day_materialized_view_retain ON CLUSTER cluster_bidev 
to banyan_bi_sdk.payment_retain_day_materialized_view
AS 
SELECT
	_t2.`date` as `date`,
	product_id,
	channel_id,
	os_type,
	os_name,
	dateDiff('day', _t2.date, toDate(_t1.`date`)) as `interval`,
    toDate(_t1.`date`) as `interval_date`,
	uniqExactArrayState(arrayIntersect(_t1.retain, _t2.initial_detail)) as `retain`
FROM
	banyan_bi_sdk.payment_retain_local as _t1 join (select `date`,product_id,channel_id,initial_detail from banyan_bi_sdk.payment_retain_day_materialized_view where `interval`=0) as _t2 using(product_id,channel_id)
where _t2.date <= toDate(_t1.`date`) and dateDiff('day', _t2.date, toDate(_t1.`date`)) <= 370
group by
	toDate(_t1.`date`),
	product_id,
	channel_id,
	os_type,
	os_name,
	_t2.date;


drop TABLE IF EXISTS banyan_bi_sdk.payment_retain_day_materialized_view_initial ON CLUSTER cluster_bidev sync;
CREATE MATERIALIZED VIEW banyan_bi_sdk.payment_retain_day_materialized_view_initial ON CLUSTER cluster_bidev 
to banyan_bi_sdk.payment_retain_day_materialized_view
AS 
select 
    `date`,
    product_id,
	channel_id,
	os_type,
	os_name,
	`number` as `interval`,
    addDays(`interval_date`, number) as `interval_date`,
    `initial`,
    `initial_detail`
from 
(
SELECT
	toDate(_t1.`date`) as `date`,
	product_id,
	channel_id,
	os_type,
	os_name,
	`interval`,
    toDate(_t1.`interval_date`) as `interval_date`,
	uniqExactArrayState(_t1.initial) as `initial`,
	groupUniqArrayArray(_t1.initial) as `initial_detail`
FROM
	banyan_bi_sdk.payment_retain_local as _t1
where `interval` = 0
group by
	toDate(_t1.`date`),
	product_id,
	channel_id,
	os_type,
	os_name,
	`interval`,
    toDate(_t1.`interval_date`)
) as _tt1,numbers(0,370) as _tt2;

drop TABLE IF EXISTS banyan_bi_sdk.payment_first_ltv_day_materialized_view_pay_fee ON CLUSTER cluster_bidev sync;
CREATE MATERIALIZED VIEW banyan_bi_sdk.payment_first_ltv_day_materialized_view_pay_fee ON CLUSTER cluster_bidev 
to banyan_bi_sdk.payment_first_ltv_day_materialized_view
AS 
select 
    `date`,
    product_id,
	channel_id,
	os_type,
	os_name,
	`number` as `interval`,
    addDays(`interval_date`, number) as `interval_date`,
    `pay_fee`
from 
(SELECT
	toDate(_t1.`date`) as `date`,
	product_id,
	channel_id,
	os_type,
	os_name,
	`interval`,
    toDate(_t1.`interval_date`) as `interval_date`,
    maxMap((arrayMap(x->xxHash32(x.1), pay_fee),arrayMap(x->x.2, pay_fee))) as pay_fee
FROM
	banyan_bi_sdk.payment_first_ltv_local as _t1
group by
	toDate(_t1.`date`),
	product_id,
	channel_id,
	os_type,
	os_name,
	`interval`,
    toDate(_t1.`interval_date`)
) as _tt1,numbers(0,370) as _tt2 where _tt2.number >= _tt1.`interval`;

drop TABLE IF EXISTS banyan_bi_sdk.view_sdk_app_web_register_payment_agg ON CLUSTER cluster_bidev sync;
CREATE TABLE banyan_bi_sdk.view_sdk_app_web_register_payment_agg ON
CLUSTER cluster_bidev as banyan_bi_sdk.sdk_app_web_register_payment_agg_local ENGINE = Distributed(cluster_bidev,
banyan_bi_sdk,
sdk_app_web_register_payment_agg_local);

ALTER TABLE banyan_bi_sdk.sdk_app_web_register_payment_agg_hour DROP PARTITION '2021-10-14';
ALTER TABLE banyan_bi_sdk.sdk_app_web_register_payment_agg_day DROP PARTITION '2021-10-14';