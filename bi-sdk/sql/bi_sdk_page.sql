drop TABLE banyan_bi_sdk.sdk_app_sdk_web_session_local on cluster cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.sdk_app_sdk_web_session_local on cluster cluster_1shards_2replicas (
 `date` DateTime COMMENT '时间',
 `product` String COMMENT '产品',
 `channel` String COMMENT '渠道',
 `source_mq` UInt8 COMMENT '0代表Sdk_App，1代表Sdk_Web',
 `os_type` UInt8 COMMENT '系统类型',
 `os` String COMMENT '系统',
 `user_type` UInt8 COMMENT '用户类型',
 `app_version` String COMMENT 'app版本',
  session String COMMENT '会话',
  page_sequence Int64 COMMENT '页面序列号',
 `in_time` DateTime COMMENT '页面进入时间',
 `page_event_type` UInt8 COMMENT '事件类型。1：start事件；2：end事件。',
  version UInt64 COMMENT '版本，用page_event_type拼上时间戳',
 `page_title` String COMMENT '页面标题',
 `page_url` String COMMENT '页面地址',
 `parent_page_url` String COMMENT '父页面地址',
 `parent_page_domain` String COMMENT '父页面域名',
  online_duration UInt64 COMMENT '在线时长',
  pv UInt64 ,
  active_user String,
  uv String,
  identifier String COMMENT 'uuid或cookie',
  ip String,
 `timeline` Date,
  insert_time DateTime DEFAULT now()
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_1shards_2replicas/{layer}-{shard02}/banyan_bi_sdk/sdk_app_sdk_web_sesseion_local', '{replica}', version) PARTITION BY timeline ORDER BY (product, session, identifier, page_sequence);

drop table banyan_bi_sdk.sdk_app_sdk_web_session_page on cluster cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.sdk_app_sdk_web_session_page on cluster cluster_1shards_2replicas
(
`date` DateTime,
`product` String,
`agent_name` String,
`channel_name` String,
`cid_name` String,
`billing_name` String,
`os_type` UInt8,
`user_type` UInt8,
`app_version` String,
`session` String,
`page_sequence` Int64,
`in_time` DateTime,
`page_event_type` UInt8 COMMENT '事件类型。1：start事件；2：end事件。',
version UInt64 COMMENT '版本，用page_event_type拼上时间戳',
`page_title` String,
`page_url` String,
`parent_page_url` String,
`parent_page_domain` String,
`online_duration` UInt64,
`pv` UInt64,
`active_user` String,
`uv` String,
 identifier String COMMENT 'uuid或cookie',
`ip` String,
`timeline` Date
)
ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/cluster_1shards_2replicas/{layer}-{shard02}/banyan_bi_sdk/sdk_app_sdk_web_session_page', '{replica}', version)
PARTITION BY timeline ORDER BY (product, session, identifier, page_sequence);

drop TABLE banyan_bi_sdk.view_sdk_app_sdk_web_session ON CLUSTER cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.view_sdk_app_sdk_web_session ON
CLUSTER cluster_1shards_2replicas as banyan_bi_sdk.sdk_app_sdk_web_session_page ENGINE = Distributed(cluster_1shards_2replicas,
banyan_bi_sdk,
sdk_app_sdk_web_session_page);

