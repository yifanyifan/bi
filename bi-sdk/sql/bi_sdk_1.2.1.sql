--drop TABLE banyan_bi_sdk.sdk_app_web_local on cluster cluster_1shards_2replicas;
--
--CREATE TABLE banyan_bi_sdk.sdk_app_web_local on cluster cluster_1shards_2replicas(
--`date` DateTime COMMENT '时间',
-- `product` String COMMENT '产品',
-- `source_mq` UInt8 COMMENT '0代表Sdk_App，1代表Sdk_Web',
-- `screen_width` String COMMENT '屏幕长',
-- `screen_height` String COMMENT '屏幕宽',
-- `channel` String COMMENT '渠道',
-- `browser` String COMMENT '浏览器',
-- `country` String COMMENT '国家',
-- `province` String COMMENT '省份',
-- `city` String COMMENT '城市',
-- `app_version` String COMMENT 'app版本',
-- `os` String COMMENT '系统',
-- `os_type` UInt8 COMMENT '系统类型(1:web;2:PC客户端;3:手机H5;4:微信页面；5：APP内嵌H5；6：ios，7：android)',
-- `os_version` String COMMENT '操作系统版本,如12.3.0',
-- `brand` String COMMENT '安装该APP的设备品牌，如APPLE,HUAWEI等',
-- `model` String COMMENT '安装该APP的设备型号,如iphone11等，',
-- `carrier` String COMMENT '运营商',
-- `user_type` UInt8 COMMENT '0代表老访客，1代表新访客',
-- `page_title` String COMMENT '页面title',
-- `page_url` String COMMENT '页面URL',
-- `pv` UInt32 COMMENT 'pv数量',
-- `startup` UInt32 COMMENT '启动次数（没有按照uid或cookie去重的统计）',
-- `uv` Array(String) COMMENT '访客。count distinct uuid',
-- `new_uv` Array(String) COMMENT '新访客',
-- `visitor` Array(String) COMMENT '游客。count(distinct uuid) where uid is null',
-- `active_user` Array(String) COMMENT '去重的uid',
-- `online_duration` UInt32 COMMENT '在线时长',
-- `ips` Array(String) COMMENT 'ip集合',
-- `start_sessions` Array(String) COMMENT '不去重的session',
-- `in_sessions` Array(String) COMMENT 'page_seq等于1的session',
-- `create_time` DateTime COMMENT '写入时间',
-- `version` UInt64 COMMENT '版本',
-- `timeline` Date COMMENT '分区字段',
-- `shard` UInt8 COMMENT '分片字段',
-- `insert_time` DateTime DEFAULT now()
--) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_1shards_2replicas/{layer}-{shard02}/banyan_bi_sdk/sdk_app_web_local', '{replica}', version) PARTITION BY timeline
--ORDER BY (date, product, source_mq, app_version, screen_width, screen_height, os, os_type, os_version, browser, brand, model, user_type, country, province, city, channel, carrier, page_title, page_url);

ALTER TABLE banyan_bi_sdk.sdk_app_web_local ADD column `visitor` Array(String) after new_uv;

ALTER TABLE banyan_bi_sdk.sdk_app_sdk_web_local ADD column `visitor` Array(String) after new_uv;

drop TABLE banyan_bi_sdk.view_sdk_app_sdk_web ON CLUSTER cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.view_sdk_app_sdk_web ON
CLUSTER cluster_1shards_2replicas as banyan_bi_sdk.sdk_app_sdk_web_local ENGINE = Distributed(cluster_1shards_2replicas,
banyan_bi_sdk,
sdk_app_sdk_web_local);

ALTER TABLE banyan_bi_sdk.sdk_app_sdk_web_register_info_local ADD column `visitor` Array(String) after new_uv;

drop TABLE banyan_bi_sdk.view_sdk_app_sdk_web_register_info ON CLUSTER cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.view_sdk_app_sdk_web_register_info ON
CLUSTER cluster_1shards_2replicas as banyan_bi_sdk.sdk_app_sdk_web_register_info_local ENGINE = Distributed(cluster_1shards_2replicas,
banyan_bi_sdk,
sdk_app_sdk_web_register_info_local);


ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d_local ADD column `os_name` String after pay_amount;
ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d_local ADD column `os_type` UInt8 after pay_amount;

ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w_local ADD column `os_name` String after pay_amount;
ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w_local ADD column `os_type` UInt8 after pay_amount;

ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m_local ADD column `os_name` String after pay_amount;
ALTER TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m_local ADD column `os_type` UInt8 after pay_amount;

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d ON CLUSTER cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_d ON
CLUSTER cluster_1shards_2replicas as banyan_bi_sdk.dws_bi_sdk_user_retain_d_local ENGINE = Distributed(cluster_1shards_2replicas,
banyan_bi_sdk,
dws_bi_sdk_user_retain_d_local);

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m ON CLUSTER cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_m ON
CLUSTER cluster_1shards_2replicas as banyan_bi_sdk.dws_bi_sdk_user_retain_m_local ENGINE = Distributed(cluster_1shards_2replicas,
banyan_bi_sdk,
dws_bi_sdk_user_retain_m_local);

drop TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w ON CLUSTER cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.dws_bi_sdk_user_retain_w ON
CLUSTER cluster_1shards_2replicas as banyan_bi_sdk.dws_bi_sdk_user_retain_w_local ENGINE = Distributed(cluster_1shards_2replicas,
banyan_bi_sdk,
dws_bi_sdk_user_retain_w_local);