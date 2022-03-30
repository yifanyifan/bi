drop TABLE banyan_bi_sdk.acc_reg_pay ON CLUSTER cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.acc_reg_pay ON CLUSTER cluster_1shards_2replicas(
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
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_1shards_2replicas/{layer}-{shard02}/banyan_bi_sdk/acc_reg_pay', '{replica}') PARTITION BY date ORDER BY (date, agg_dim, product, agent_name, channel_name, cid_name, billing_name);

drop TABLE banyan_bi_sdk.acc_sdk_app_web ON CLUSTER cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.acc_sdk_app_web ON CLUSTER cluster_1shards_2replicas(
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
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/cluster_1shards_2replicas/{layer}-{shard02}/banyan_bi_sdk/acc_sdk_app_web', '{replica}') PARTITION BY date ORDER BY (date, agg_dim, product, agent_name, channel_name, cid_name, billing_name);



drop TABLE banyan_bi_sdk.view_acc_reg_pay ON CLUSTER cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.view_acc_reg_pay ON
CLUSTER cluster_1shards_2replicas as banyan_bi_sdk.acc_reg_pay ENGINE = Distributed(cluster_1shards_2replicas,
banyan_bi_sdk,
acc_reg_pay);

drop TABLE banyan_bi_sdk.view_acc_sdk_web ON CLUSTER cluster_1shards_2replicas;
CREATE TABLE banyan_bi_sdk.view_acc_sdk_web ON
CLUSTER cluster_1shards_2replicas as banyan_bi_sdk.acc_sdk_app_web ENGINE = Distributed(cluster_1shards_2replicas,
banyan_bi_sdk,
acc_sdk_app_web);

drop table banyan_bi_sdk.view_acc_sdk_web_reg_pay ON CLUSTER cluster_1shards_2replicas;
CREATE VIEW banyan_bi_sdk.view_acc_sdk_web_reg_pay ON CLUSTER cluster_1shards_2replicas(
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
 billing_name)
