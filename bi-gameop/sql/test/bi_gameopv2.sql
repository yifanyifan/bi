/** 创建游戏运营数据库 */
create database bi_gameop on cluster cluster_1shards_2replicas;

/**
 * 归因逻辑
 */

/**
  设备新增
 */
create table if not exists  bi_gameop.dwd_new_dev on cluster cluster_1shards_2replicas
(
    `business`     String comment '业务归属 yilewan、yfy',
    `game_code`    String comment '游戏CODE',
    `dev_id`       String comment '设备ID',
    `pid`          String comment 'PID',
    `receive_time` UInt64 comment '时间戳(秒)',
    `update_time`  UInt64 comment '归因更新时间'
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dwd_new_dev',
                                        '{replica}', update_time)
    PARTITION BY (business)
    ORDER BY
(game_code, dev_id) SETTINGS index_granularity = 8192;

/**
 * 用户归因
 */
create table if not exists  bi_gameop.dwd_new_user on cluster cluster_1shards_2replicas
(
    `business`     String comment '业务归属 yilewan、yfy',
    `game_code`    String comment '游戏CODE',
    `uid`          String comment '用户ID',
    `dev_id`       String comment '第一次绑定的设备ID',
    `pid`          String comment 'PID',
    `channel_game` String comment 'A类游戏',
    `receive_time` UInt64 comment '时间戳(秒)',
    `update_time`  UInt64 comment '归因更新时间'
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dwd_new_user',
                                        '{replica}', update_time)
    PARTITION BY (business)
    ORDER BY
(game_code, uid) SETTINGS index_granularity = 8192;

/**
 * 角色归因
 */
create table if not exists  bi_gameop.dwd_new_role on cluster cluster_1shards_2replicas
(
    `business`     String comment '业务归属 yilewan、yfy',
    `game_code`    String comment '游戏CODE',
    `role_id`      String comment '角色ID',
    `uid`          String comment '用户ID',
    `dev_id`       String comment '设备ID',
    `game_server`  String comment '游戏区服',
    `pid`          String comment 'PID',
    `receive_time` UInt64 comment '时间戳(秒)',
    `update_time`  UInt64 comment '归因更新时间'
) ENGINE = ReplicatedReplacingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dwd_new_role',
                                        '{replica}', update_time)
    PARTITION BY (business)
    ORDER BY
(game_code, role_id, uid) SETTINGS index_granularity = 8192;


/**
  包含 用户 和  设备
 */
create table if not exists  bi_gameop.ods_uc_login_info on cluster cluster_1shards_2replicas
(
-- >> 分区字段
    `partition_date` Date comment '分区字段',
-- >> 源数据
    `uid`            String comment '用户ID',
    `dev_id`         String comment '设备ID',
    `business`       String comment 'Business',
    `business_type`  String comment '业务类型，与business关联，进一步细分业务，由业务部门按实际需求划分',
    `game_code`      String comment '游戏CODE, 同app_code, 易乐玩会从attr_value优先获取',
    `pid`            String comment 'PID',
    `action_type`    UInt8 comment '1登录 2注册',
    `source_page`    String comment '来源页',
    `channel_game`   String comment 'remark_ext中解析出channel_game',
    `channel_code`   String comment 'remark_ext中解析出channel_code',
    `show_url`       String comment '当前页面的url地址，上报时需进行urlencode',
    `refer_url`      String comment '跳转来源页面的url地址，上报时需进行urlencode',
    `send_time`      UInt64 comment '事件发生的时间戳',
    `client_ip`      String comment '客户端IP',
    `receive_time`   UInt64 comment 'receive_time',
-- >> >> 设备标识
    `os_type`        UInt8 comment '客户端系统标识：1:PC_Web； 2:iOS；3:Android； 4:WAP（手机H5） 5:APP内嵌H5； 6:微信小程序 ； 7:PC客户端；8:TV端',
    `os_ver`         String comment '客户端系统版本号',
    `idfa`           String comment 'IDFA',
    `imei`           String comment 'IMEI',
    `oaid`           String comment 'OAID',
    `nua`            String comment 'NUA',
-- >> 扩展字段
    `country`        String default '中国' comment '国家',
    `province`       String comment '省',
    `city`           String comment '市',
    `area`           String comment '区',
    `carrier`        String comment '运营商名称，例如中国移动,中国联通,中国电信,其他'
)
    ENGINE = ReplicatedMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/ods_uc_login_info',
                                 '{replica}') PARTITION BY (partition_date)
    ORDER BY
(business, game_code, pid, uid) SETTINGS index_granularity = 8192;


/**
  包含角色 和 用户
 */
create table if not exists  bi_gameop.ods_yilewan_game_role on cluster cluster_1shards_2replicas
(
-- >> 分区字段
    `partition_date`     Date comment '分区字段',
-- >> 源数据
    `receive_time`       UInt64 comment 'receive_time',
    `game_code`          String comment '游戏CODE',
    `channel_id`         String comment '渠道标识(即 pid)，游戏发行传pid，云飞扬传ccid',
    `send_time`          UInt32 comment '时间戳',
    `uid`                String comment 'UID, 如果business=云飞扬,则取yfy_uid',
    `player_role_id`     String comment '玩家角色ID',
    `player_role_name`   String comment '玩家角色名称',
    `player_server_id`   String comment '玩家区服ID',
    `player_server_name` String comment '玩家区服名称',
    `player_grade`       String comment '玩家等级',
    `channel_name`       String comment '云飞扬渠道名称 halou、douyu、2144等',
    `player_rein`        String comment '转生次数',
    `player_power`       String comment '战力',
    `player_sex`         String comment '角色性别',
    `player_vip_level`   String comment '角色VIP等级',
    `business`           String comment '业务线',
    `business_type`      String comment '业务线类型',
    `action_type`        UInt8 comment '1：创建角色 2：等级提升 3：vip等级改变 4.创建账号 5.角色上线 6.角色下线 7.用户充值 8.选择服务器 9.进入游戏'

) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/ods_yilewan_game_role',
                               '{replica}') PARTITION BY (partition_date)
    ORDER BY
(business, game_code, uid, player_server_id, player_role_id) SETTINGS index_granularity = 8192;


/**
  包含 设备 和 用户
 */
create table if not exists  bi_gameop.ods_sdk_app_003 on cluster cluster_1shards_2replicas
(
-- >> 分区字段
    `partition_date` Date comment '分区日期',
-- >> 源数据
    `receive_time`   UInt64 comment 'receive_time',
    `game_code`      String comment '游戏CODE, 同product_id',
    `os_type`        UInt8 comment '客户端系统标识：1:PC_Web； 2:iOS；3:Android； 4:WAP（手机H5） 5:APP内嵌H5； 6:微信小程序 ； 7:PC客户端；8:TV端',
    `os`             String comment '操作系统，如iOS、Android',
    `os_version`     String comment '操作系统版本,如12.3.0',
    `brand`          String comment '安装该APP的设备品牌，如APPLE,HUAWEI等',
    `model`          String comment '安装该APP的设备型号,如iphone11等',
    `client_ip`      String comment '取自source_ip',
    `channel_id`     String comment '渠道来源名字或者ID',
    `pid`            String comment 'PID',
    `uid`            String comment '用户中心UID',
    `idfa`           String comment 'IDFA',
    `imei`           String comment 'IMEI',
    `oaid`           String comment 'OAID',
    `nua`            String comment 'NUA',
    `dev_id`         String comment '设备ID',
    `event_time`     UInt64 comment '事件发生时间',
    `event_type`     String comment '预置事件类型，如：pageview',
    -- >> 扩展字段
    `country`        String default '中国' comment '国家',
    `province`       String comment '省',
    `city`           String comment '市',
    `area`           String comment '区',
    `carrier`        String comment '运营商名称，例如中国移动,中国联通,中国电信,其他'

) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/ods_sdk_app_003',
                               '{replica}') PARTITION BY (partition_date)
    ORDER BY
(game_code, dev_id) SETTINGS index_granularity = 8192;


drop table bi_gameop.ods_bc_payment_info on cluster cluster_1shards_2replicas sync;

/**
* ODS
*/
create table if not exists  bi_gameop.ods_bc_payment_info on cluster cluster_1shards_2replicas
(
-- >> 分区字段
    `partition_date`   Date comment '分区字段',
-- >> 源数据
    `receive_time`     UInt64 comment 'receive_time',
    `os_type`          UInt8 comment '客户端系统标识：1:PC_Web； 2:iOS；3:Android； 4:WAP（手机H5） 5:APP内嵌H5； 6:微信小程序 ； 7:PC客户端；8:TV端',
    `os_ver`           String comment '客户端系统版本号',
    `dev_id`           String comment '设备ID',
    `client_ip`        String comment '客户端IP',
    `uid`              String comment '用户中心UID',
    `pid`              String comment '充值PID(归因之后的PID)',
    `business`         String comment '业务',
    `server_id`        String comment 'attr_ext中解析出游戏区服ID',
    `server_name`      String comment 'attr_ext中解析出游戏区服名称',
    `role_id`          String comment 'attr_ext中解析出游戏角色ID',
    `role_name`        String comment 'attr_ext中解析出游戏角色名称',
    `action_type`      UInt8 comment '事件类型：1. 下单；2. 支付成功；3. 支付失败；4. 支付超时；4. 退款',
    `product_order_id` String comment '产品订单ID（业务方生成的订单ID）',
    `product_price`    Float64 comment '产品总价格（订单商品本身的定价，非支付价格）',
    `pay_order_id`     String comment '支付订单ID（由支付中心生成）',
    `pay_fee`          Float64 comment '支付金额',
    `pay_time`         UInt64 comment '支付时间（unixtime时间戳，精确到秒）',
    `consume_order_id` String comment '消费订单ID（由支付中心生成，用于通知业务方）',
    `game_code`        String comment '游戏CODE, 同app_code, business=易乐玩时通过解析attr_value得到',
    `coupon_fee`       Float64 comment '订单支付时，使用优惠券抵用的总金额',
    `coupon_name`      String comment '使用的优惠券名称，如红包、折扣券、优惠券',
    `coin_fee`         Float64 comment '使用金币的抵用总金额',
    `coin_name`        String comment '使用的金币名称，如游戏币、E币、钻石币',
    `send_time`        UInt64 comment '事件发生的时间戳',
    `idfa`             String comment 'IDFA',
    `imei`             String comment 'IMEI',
    `oaid`             String comment 'OAID',
    `nua`              String comment 'NUA',
    -- >> 扩展字段
    `country`          String default '中国' comment '国家',
    `province`         String comment '省',
    `city`             String comment '市',
    `area`             String comment '区',
    `carrier`          String comment '运营商名称，例如中国移动,中国联通,中国电信,其他'

) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/ods_bc_payment_info',
                               '{replica}') PARTITION BY (partition_date)
    ORDER BY
(business, game_code, pid, uid) SETTINGS index_granularity = 8192;


/**
  易乐玩和发行的同表结构, 不同表
 */
create table if not exists  bi_gameop.dwd_publish on cluster cluster_1shards_2replicas
(
    `dim_type`         String comment '统计类型：user.用户；dev.设备；role.角色',
    `partition_date`   Date comment '分区日期',
    --  >>
    `partition_hour`   DateTime comment '小时',
    `partition_minute` DateTime comment '分钟',
    `origin`           UInt8 comment '事件源:1.激活,2.登录,3.角色,4.付费',
    `business`         String comment 'business',
    `game_code`        String comment '游戏CODE',
    `pid`              String comment 'PID, 归因后PID',
    `game_server`      String comment '区服ID',
    `province`         String comment '省',
    `id`               String comment 'uid 或者 dev_id 或者 player_role_Id',
    `uid`              String comment 'UID, 在做角色新增判断时要用到',
    `channel_game`     String comment 'channel_game',
    `is_new`           UInt8   default 0 comment '是否新增(日)',
    `is_new_user`      UInt8   default 0 comment '角色数据中是否新增用户',
    `new_date`         Date comment '新增日期',
    `is_week_new`      UInt8   default 0 comment '是否周新增',
    `is_month_new`     UInt8   default 0 comment '是否月新增',
    `is_first`         UInt8   default 0 comment '用于角色滚服 和 付费首次',
    `action_type`      UInt8 comment '事件类型子类型, 当source=1时, action_type=1(登录), 2(注册)',
    `os`               String comment '操作系统，如iOS、Android',
    `os_version`       String comment '操作系统版本,如12.3.0',
    `brand`            String comment '安装该APP的设备品牌，如APPLE,HUAWEI等',
    `model`            String comment '安装该APP的设备型号,如iphone11等',
    `pay_id`           String comment '付费订单ID, 取product_order_id',
    `pay_fee`          Float64 default 0 comment '总付费金额',
    `pay_e`            Float64 default 0 comment 'E币金额',
    `pay_coupon`       Float64 default 0 comment '优惠券充值金额',
    `pay_rmb`          Float64 default 0 comment 'RMB充值金额',
    `receive_time`     UInt64 comment 'receive_time',
    `update_time`      UInt64 comment 'update_time'
) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dwd_publish',
                               '{replica}') PARTITION BY (dim_type, partition_date)
    ORDER BY
(origin, business, game_code, pid, game_server) SETTINGS index_granularity = 8192;


create table if not exists  bi_gameop.dws_publish_global on cluster cluster_1shards_2replicas
(
-- >> 分区字段
    `dim_type`            String comment '统计类型：user.用户；dev.设备；role.角色',
    `partition_date`      Date comment '分区字段',
-- >> 维度
    `partition_hour`      DateTime comment '小时',
    `partition_minute`    DateTime comment '分钟',
    `business`            String comment 'business',
    `pid`                 String comment 'PID',
    `game_code`           String comment '游戏CODE',
    `game_server`         String comment '区服ID',
    `is_a`                UInt8 comment '是否A类游戏, 1表示A类',
    `province`            String comment '省份',
-- >> 随机数
    `nonce`               UInt64 comment '随机数',
-- >> 新增
    `reg_arr`             Array(String) comment '注册人数',
    `login_new_arr`       Array(String) comment '登录新增人数',
    `new_arr`             Array(String) comment '新增人数数组',
    `unreal_new_arr`      Array(String) comment '新增人数数组(未实名)',
    `active_arr`          Array(String) comment '活跃人数',
    `valid_new_arr`       Array(String) comment '有效新增设备数',
    `unreal_valid_new_arr` Array(String) comment '有效新增设备数(未实名)',
    `create_role_arr`     Array(String) comment '创角数(创建角色的账号)',
    `create_role_new_arr` Array(String) comment '新增创角账号数',
    `roll_login_arr`      Array(String) comment '滚服登录人数',
-- >> 付费
    `pay_total_unit_arr`  Array(String) comment '总付费数,指付费的人、设备和角色',
    `pay_total_num`       UInt64 comment '总付费次数',
    `pay_total_money`     Float64 comment '总付费金额',
    `pay_total_e`         Float64 comment 'E币金额',
    `pay_total_coupon`    Float64 comment '优惠券充值金额',
    `pay_total_rmb`       Float64 comment 'RMB充值金额',
    `pay_new_unit_arr`    Array(String) comment '新增付费数, 按人、设备、角色统计',
    `pay_new_num`         UInt64 comment '新增付费次数',
    `pay_new_money`       Float64 comment '新增付费金额',
    `pay_first_unit_arr`  Array(String) comment '首次付费数, 按人、设备、角色统计',
    `pay_first_num`       UInt64 comment '首次付费次数',
    `pay_first_money`     Float64 comment '首次付费金额'
    ) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dws_publish_global',
                                   '{replica}') PARTITION BY (dim_type, partition_date)
    ORDER BY
(partition_hour, partition_minute, business, pid, game_code,
 game_server, is_a, province) SETTINGS index_granularity = 8192;

drop table bi_gameop.dw_publish_retention on cluster cluster_1shards_2replicas sync;
/**
 * 留存表
 */
create table if not exists  bi_gameop.dw_publish_retention on cluster cluster_1shards_2replicas
(
    `ret_date`      Date comment '留存日期',
    `new_date`      Date comment '新增日期',
    `date_gap`      UInt8 comment '间距',
    `dim_type`      String comment '统计类型：user.用户；dev.设备；role.角色',
    `business`      String comment 'business',
    `game_code`     String comment '游戏CODE',
    `pid`           String comment 'PID',
    `game_server`   String comment '区服ID',
    `new_arr`       Array(String) comment '新增即初始值',
    `active_arr`    Array(String) comment '活跃',
    `pay_arr`       Array(String) comment '付费',
    `new_pay_arr`   Array(String) comment '新增付费',
    `pay_money`     Float64 comment '付费金额'
    ) ENGINE = ReplicatedMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dw_publish_retention',
                                   '{replica}') PARTITION BY (ret_date)
    ORDER BY
(new_date, business, dim_type, pid, game_code, game_server) SETTINGS index_granularity = 8192;


/**
* 游戏发行聚合表
*/
create table if not exists  bi_gameop.dws_publish_global_agg on cluster cluster_1shards_2replicas
(
-- >> 分区字段
    `dim_type`            String comment '统计类型：user.用户；dev.设备；role.角色',
    `partition_date`      Date comment '分区字段',
-- >> 维度
    `partition_hour`      DateTime comment '小时',
    `partition_minute`    DateTime comment '分钟',
    `business`            String comment 'business',
    `pid`                 String comment 'PID',
    `game_code`           String comment '游戏CODE',
    `game_server`         String comment '区服ID',
    `is_a`                UInt8 comment '是否A类游戏, 1表示A类',
    `province`            String comment '省份',
    -- >> 新增
    `reg_arr`             SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '注册人数',
    `login_new_arr`       SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '登录新增人数',
    `new_arr`             SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增人数数组',
    `unreal_new_arr`      SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增人数数组(未实名)',
    `active_arr`          SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '活跃人数',
    `valid_new_arr`       SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '有效新增设备数',
    `unreal_valid_new_arr` SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '有效新增设备数(未实名)',
    `create_role_arr`     SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '创角数(创建角色的账号)',
    `create_role_new_arr` SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增创角账号数',
    `roll_login_arr`      SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '滚服登录人数',
-- >> 付费
    `pay_total_unit_arr`  SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '总付费数,指付费的人、设备和角色',
    `pay_total_num`       SimpleAggregateFunction(sum, UInt64) comment '总付费次数',
    `pay_total_money`     SimpleAggregateFunction(sum, Float64) comment '总付费金额',
    `pay_total_e`         SimpleAggregateFunction(sum, Float64) comment 'E币金额',
    `pay_total_coupon`    SimpleAggregateFunction(sum, Float64) comment '优惠券充值金额',
    `pay_total_rmb`       SimpleAggregateFunction(sum, Float64) comment 'RMB充值金额',
    `pay_new_unit_arr`    SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费数, 按人、设备、角色统计',
    `pay_new_num`         SimpleAggregateFunction(sum, UInt64) comment '新增付费次数',
    `pay_new_money`       SimpleAggregateFunction(sum, Float64) comment '新增付费金额',
    `pay_first_unit_arr`  SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '首次付费数, 按人、设备、角色统计',
    `pay_first_num`       SimpleAggregateFunction(sum, UInt64) comment '首次付费次数',
    `pay_first_money`     SimpleAggregateFunction(sum, Float64) comment '首次付费金额'
    ) ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dws_publish_global_agg',
                                              '{replica}')
    PARTITION BY (dim_type, partition_date)
    ORDER BY
(partition_hour, partition_minute, business, pid, game_code, game_server, is_a,
 province) SETTINGS index_granularity = 8192;


/**
* 创建视图视图 合并dws层数据
*/
create materialized view bi_gameop.dws_publish_global_2agg_mv
on cluster cluster_1shards_2replicas
to bi_gameop.dws_publish_global_agg as
select dim_type,
       partition_date,
       partition_hour,
       partition_minute,
       business,
       pid,
       game_code,
       game_server,
       is_a,
       province,
       -- 活跃指标
       groupUniqArrayArray(reg_arr) as reg_arr,
       groupUniqArrayArray(login_new_arr) as login_new_arr,
       groupUniqArrayArray(new_arr) as new_arr,
       groupUniqArrayArray(unreal_new_arr) as unreal_new_arr,
       groupUniqArrayArray(active_arr) as active_arr,
       groupUniqArrayArray(valid_new_arr) as valid_new_arr,
       groupUniqArrayArray(unreal_valid_new_arr) as unreal_valid_new_arr,
       groupUniqArrayArray(create_role_arr) as create_role_arr,
       groupUniqArrayArray(create_role_new_arr) as create_role_new_arr,
       groupUniqArrayArray(roll_login_arr) as roll_login_arr,
       -- 付费
       groupUniqArrayArray(pay_total_unit_arr) as pay_total_unit_arr,
       sum(pay_total_num) as pay_total_num,
       sum(pay_total_money) as pay_total_money,
       sum(pay_total_e) as pay_total_e,
       sum(pay_total_coupon) as pay_total_coupon,
       sum(pay_total_rmb) as pay_total_rmb,
       groupUniqArrayArray(pay_new_unit_arr) as pay_new_unit_arr,
       sum(pay_new_num) as pay_new_num,
       sum(pay_new_money) as pay_new_money,
       groupUniqArrayArray(pay_first_unit_arr) as pay_first_unit_arr,
       sum(pay_first_num) as pay_first_num,
       sum(pay_first_money) as pay_first_money
from bi_gameop.dws_publish_global
group by dim_type, partition_date, partition_hour, partition_minute, business, pid, game_code, game_server, is_a,
         province;


drop table bi_gameop.dw_publish_retention_agg on cluster cluster_1shards_2replicas sync;
/**
   * 聚合引擎的留存表
   */
create table if not exists  bi_gameop.dw_publish_retention_agg on cluster cluster_1shards_2replicas
(
    `ret_date`    Date comment '留存日期',
    `new_date`    Date comment '新增日期',
    `date_gap`    UInt8 comment '间距',
    `dim_type`    String comment '统计类型：user.用户；dev.设备；role.角色',
    `business`    String comment 'business',
    `game_code`   String comment '游戏CODE',
    `pid`         String comment 'PID',
    `game_server` String comment '区服ID',
    `new_arr`     SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增',
    `pay_arr`     SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '付费',
    `new_pay_arr` SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费',
    `active_arr`  SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '活跃',
    `pay_money`   SimpleAggregateFunction(sum, Float64) comment '付费金额'
    ) ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dw_publish_retention_agg',
                                              '{replica}')
    PARTITION BY (ret_date)
    ORDER BY
(date_gap, dim_type, business, game_code, pid, game_server) SETTINGS index_granularity = 8192;



drop table bi_gameop.dw_publish_retention_2agg_mv on cluster cluster_1shards_2replicas sync;
/**
* 留存的聚合引擎
*/
CREATE MATERIALIZED VIEW bi_gameop.dw_publish_retention_2agg_mv
on cluster cluster_1shards_2replicas
to bi_gameop.dw_publish_retention_agg
as
select ret_date,
       new_date,
       date_gap,
       dim_type,
       business,
       game_code,
       pid,
       game_server,
       groupUniqArrayArray(new_arr)       as new_arr,
       groupUniqArrayArray(pay_arr)       as pay_arr,
       groupUniqArrayArray(new_pay_arr)   as new_pay_arr,
       groupUniqArrayArray(active_arr)    as active_arr,
       sum(pay_money)                     as pay_money
from bi_gameop.dw_publish_retention
group by ret_date, new_date, date_gap, dim_type, business, game_code, pid, game_server ;


drop table bi_gameop.dw_publish_retention_init_2agg_mv on cluster cluster_1shards_2replicas sync;
/**
 * 将0初始值同步到日留存表
 */
CREATE MATERIALIZED VIEW bi_gameop.dw_publish_retention_init_2agg_mv
on cluster cluster_1shards_2replicas
to bi_gameop.dw_publish_retention_agg
as
select addDays(ret_date, `number`) as ret_date ,
       new_date ,
       `number` as date_gap ,
       dim_type ,
       business ,
       game_code ,
       pid ,
       game_server ,
       new_arr
from (
         select ret_date,
                new_date,
                date_gap,
                dim_type,
                business,
                game_code,
                pid,
                game_server,
                groupUniqArrayArray(new_arr)     as new_arr
         from bi_gameop.dw_publish_retention where date_gap = 0
         group by ret_date, new_date, date_gap, dim_type, business, game_code, pid, game_server ) as t1, numbers(0,180) as t2;



drop table if exists bi_gameop.dw_publish_retention_agg_week on cluster cluster_1shards_2replicas sync;
/**
 * 周留存
 */
create table if not exists  bi_gameop.dw_publish_retention_agg_week on cluster cluster_1shards_2replicas
(
    `ret_date`    Date comment '留存日期',
    `new_date`    Date comment '新增日期',
    `date_gap`    UInt8 comment '间距',
    `dim_type`    String comment '统计类型：user.用户；dev.设备；role.角色',
    `business`    String comment 'business',
    `game_code`   String comment '游戏CODE',
    `pid`         String comment 'PID',
    `game_server` String comment '区服ID',
    `new_arr`     SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增',
    `pay_arr`     SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '付费',
    `new_pay_arr` SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费',
    `active_arr`  SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '活跃',
    `pay_money`   SimpleAggregateFunction(sum, Float64) comment '付费金额'
    ) ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dw_publish_retention_agg_week',
                                              '{replica}')
    PARTITION BY (ret_date)
    ORDER BY
(date_gap, dim_type, business, game_code, pid, game_server) SETTINGS index_granularity = 8192;


drop table bi_gameop.dw_publish_retention_2agg_week_mv on cluster cluster_1shards_2replicas sync;
/**
 * 周留存 物化视图  将 日 数据写入到周中
 */
CREATE MATERIALIZED VIEW bi_gameop.dw_publish_retention_2agg_week_mv
on cluster cluster_1shards_2replicas
to bi_gameop.dw_publish_retention_agg_week
as
select toStartOfWeek(ret_date, 1) as ret_date,
       toStartOfWeek(new_date, 1) as new_date,
       dateDiff('week', toStartOfWeek(new_date, 1), toStartOfWeek(ret_date, 1)) as date_gap,
       dim_type,
       business,
       game_code,
       pid,
       game_server,
       groupUniqArrayArray(new_arr)     as new_arr,
       groupUniqArrayArray(pay_arr)     as pay_arr,
       groupUniqArrayArray(new_pay_arr) as new_pay_arr,
       groupUniqArrayArray(active_arr)  as active_arr,
       sum(pay_money)                   as pay_money
from bi_gameop.dw_publish_retention_agg as ret
group by toStartOfWeek(ret.ret_date, 1), toStartOfWeek(ret.new_date, 1), dim_type, business, game_code, pid, game_server ;


drop table bi_gameop.dw_publish_retention_agg_month on cluster cluster_1shards_2replicas sync;
/**
 * 月留存
 */
create table if not exists  bi_gameop.dw_publish_retention_agg_month on cluster cluster_1shards_2replicas
(
    `ret_date`    Date comment '留存日期',
    `new_date`    Date comment '新增日期',
    `date_gap`    UInt8 comment '间距',
    `dim_type`    String comment '统计类型：user.用户；dev.设备；role.角色',
    `business`    String comment 'business',
    `game_code`   String comment '游戏CODE',
    `pid`         String comment 'PID',
    `game_server` String comment '区服ID',
    `new_arr`     SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增',
    `pay_arr`     SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '付费',
    `new_pay_arr` SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费',
    `active_arr`  SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '活跃',
    `pay_money`   SimpleAggregateFunction(sum, Float64) comment '付费金额'
    ) ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dw_publish_retention_agg_month',
                                              '{replica}')
    PARTITION BY (ret_date)
    ORDER BY
(date_gap, dim_type, business, game_code, pid, game_server) SETTINGS index_granularity = 8192;


drop table bi_gameop.dw_publish_retention_2agg_month_mv on cluster cluster_1shards_2replicas sync;
/**
 * 月留存 物化视图  将 日留存数据写入月中
 */
CREATE MATERIALIZED VIEW bi_gameop.dw_publish_retention_2agg_month_mv
on cluster cluster_1shards_2replicas
to bi_gameop.dw_publish_retention_agg_month
as
select toStartOfMonth(ret_date) as ret_date,
       toStartOfMonth(new_date) as new_date,
       dateDiff('month', toStartOfMonth(new_date), toStartOfMonth(ret_date)) as date_gap,
       dim_type,
       business,
       game_code,
       pid,
       game_server,
       groupUniqArrayArray(new_arr)     as new_arr,
       groupUniqArrayArray(pay_arr)     as pay_arr,
       groupUniqArrayArray(new_pay_arr) as new_pay_arr,
       groupUniqArrayArray(active_arr)  as active_arr,
       sum(pay_money)                   as pay_money
from bi_gameop.dw_publish_retention_agg as ret
group by toStartOfMonth(ret.ret_date), toStartOfMonth(ret.new_date), dim_type, business, game_code, pid, game_server ;


drop table bi_gameop.dws_publish_pay_analy on cluster cluster_1shards_2replicas sync;

/**
 * 付费分析
 */
create table if not exists  bi_gameop.dws_publish_pay_analy on cluster cluster_1shards_2replicas(
                                                                                                    `partition_date` Date,
                                                                                                    `business` String comment '业务',
                                                                                                    `pid` String comment 'PID',
                                                                                                    `game_code` String comment '游戏code',
                                                                                                    `game_server` String comment '游戏区服',
                                                                                                    `uid` String comment 'uid',
--                                                                         `charge_num_arr` SimpleAggregateFunction(groupUniqArrayArray, Array(UInt64)) comment '充值次数',
                                                                                                    `charge_money_arr` SimpleAggregateFunction(groupUniqArrayArray, Array(String)) comment '充值金额'
    ) ENGINE = ReplicatedAggregatingMergeTree(
                                                 '/clickhouse/tables/{layer}-{shard02}/bi_gameop/dws_publish_pay_analy',
                                                 '{replica}'
                                             ) PARTITION BY (partition_date)
    ORDER BY
(business, pid, game_code, game_server, uid) SETTINGS index_granularity = 8192;


drop table bi_gameop.dwd_publish_2pay_analy_mv on cluster cluster_1shards_2replicas sync;
/**
 *
 */
create materialized view bi_gameop.dwd_publish_2pay_analy_mv
on cluster cluster_1shards_2replicas
to bi_gameop.dws_publish_pay_analy as
select
    partition_date,
    business,
    pid,
    game_code,
    game_server,
    id as uid,
--    groupUniqArray(pay_id) as charge_num_arr,
    groupUniqArray(concat(pay_id, ':', toString(pay_fee))) as charge_money_arr
from
    bi_gameop.dwd_publish dp
where
        dim_type = 'user'
  and dp.origin = 4
  and action_type = 2
  and pay_fee > 0
group by
    partition_date,
    business,
    pid,
    game_code,
    game_server,
    id;

select arraySum(x -> toFloat64(splitByChar(':',  x)[2]), array('A:1', 'B:2'));

select t.charge_money_arr, arraySum(x -> toFloat64OrZero(splitByChar(':',  x)[2]), t.charge_money_arr) from (
                                                                                                                select
                                                                                                                    partition_date,
                                                                                                                    business,
                                                                                                                    pid,
                                                                                                                    game_code,
                                                                                                                    game_server,
                                                                                                                    id as uid,
                                                                                                                    groupUniqArray(concat(pay_id, ':', toString(pay_fee))) as charge_money_arr
                                                                                                                from
                                                                                                                    bi_gameop.dwd_publish dp
                                                                                                                where
                                                                                                                        dim_type = 'user'
                                                                                                                  and dp.origin = 4
                                                                                                                  and action_type = 2
                                                                                                                  and pay_fee > 0
                                                                                                                group by
                                                                                                                    partition_date,
                                                                                                                    business,
                                                                                                                    pid,
                                                                                                                    game_code,
                                                                                                                    game_server,
                                                                                                                    id ) t ;

--create view bi_gameop.dws_publish_pay_analy_view on cluster cluster_1shards_2replicas as
    --select
--    partition_date,
--    business,
--    pid,
--    game_code,
--    game_server,
--    uid,
--    charge_num,
--    charge_money pid.username as create_username,
--    pid.product_name as game_name,
--    pid.department_code as department_code,
--    pid.pid_alias as pid_alias,
--    pid.channel_name as channel_name,
--    pid.sub_channel_name as sub_channel_name,
--    pid.medium_name as medium,
--    pid.charge_rule as charge_rule,
--    game.game_category_name as game_category_name,
--    pid.pid_alias as batch,
--    pid.pid_alias as account
--from
--    bi_gameop.dws_publish_pay_analy m
--    left join bi_gameop.dim_pid pid on m.pid = pid.pid
--    left join bi_gameop.yilewan_dim_game_info game on m.game_code = game.game_code;

drop table bi_gameop.dws_publish_dev_dis on cluster cluster_1shards_2replicas sync;
/**
 * 设备分析
 */
create table if not exists  bi_gameop.dws_publish_dev_dis on cluster cluster_1shards_2replicas(
                                                                                                  `partition_date` Date,
                                                                                                  `business` String comment '业务',
                                                                                                  `pid` String comment 'PID',
                                                                                                  `game_code` String comment '游戏code',
                                                                                                  `game_server` String comment '游戏区服',
                                                                                                  `os` String,
                                                                                                  `os_version` String,
                                                                                                  `brand` String,
                                                                                                  `model` String,
                                                                                                  `dev_arr` SimpleAggregateFunction(groupUniqArrayArray, Array(String)) comment '设备汇总'
    ) ENGINE = ReplicatedAggregatingMergeTree(
                                                 '/clickhouse/tables/{layer}-{shard02}/bi_gameop/dws_publish_dev_dis',
                                                 '{replica}'
                                             ) PARTITION BY (partition_date)
    ORDER BY
(business, pid, game_code, game_server) SETTINGS index_granularity = 8192;

drop table bi_gameop.dwd_publish_2dev_dis_mv on cluster cluster_1shards_2replicas sync;

create materialized view bi_gameop.dwd_publish_2dev_dis_mv on cluster cluster_1shards_2replicas to bi_gameop.dws_publish_dev_dis as
select
    partition_date,
    business,
    pid,
    game_code,
    game_server,
    os,
    os_version,
    brand,
    model,
    groupUniqArray(id) as dev_arr
from
    bi_gameop.dwd_publish dp
where
        dim_type = 'dev'
  and origin = 1
group by
    partition_date,
    business,
    pid,
    game_code,
    game_server,
    os,
    os_version,
    brand,
    model;


drop table bi_gameop.dws_publish_user_login on cluster cluster_1shards_2replicas sync;
/**
 * 登录分析
 */
create table if not exists  bi_gameop.dws_publish_user_login on cluster cluster_1shards_2replicas(
                                                                                                     `partition_date` Date,
                                                                                                     `business` String comment '业务',
                                                                                                     `pid` String comment 'PID',
                                                                                                     `game_code` String comment '游戏code',
                                                                                                     `game_server` String comment '游戏区服',
                                                                                                     `uid` String,
                                                                                                     `login_arr` SimpleAggregateFunction(groupUniqArrayArray, Array(UInt64)) comment '登录次数'
    ) ENGINE = ReplicatedAggregatingMergeTree(
                                                 '/clickhouse/tables/{layer}-{shard02}/bi_gameop/dws_publish_user_login',
                                                 '{replica}'
                                             ) PARTITION BY (partition_date)
    ORDER BY
(business, pid, game_code, game_server, uid) SETTINGS index_granularity = 8192;

drop table bi_gameop.dwd_publish_2user_login_mv on cluster cluster_1shards_2replicas sync;

create materialized view bi_gameop.dwd_publish_2user_login_mv on cluster cluster_1shards_2replicas to bi_gameop.dws_publish_user_login as
select
    partition_date,
    business,
    pid,
    game_code,
    game_server,
    uid,
    groupUniqArray(receive_time) as login_arr
from
    bi_gameop.dwd_publish dp
where
        dim_type = 'user'
  and origin = 2
  and action_type = 1
group by
    partition_date,
    business,
    pid,
    game_code,
    game_server,
    uid;


drop table bi_gameop.dws_publish_pay_analy_view on cluster cluster_1shards_2replicas sync;
/**
 * 付费分析视图
 */
create view bi_gameop.dws_publish_pay_analy_view on cluster cluster_1shards_2replicas as
select
    partition_date,
    m.business as business,
    m.pid as pid,
    m.game_code as game_code,
    game_server,
    uid,
    arrayUniq(m.charge_money_arr) as charge_num,
    arraySum(x -> toFloat64OrZero(splitByChar(':',  x)[2]), m.charge_money_arr) as charge_money,
    pid.username as create_username,
    game.game_name as game_name,
    pid.department_code as department_code,
    pid.company_name as company_name,
    pid.application_name as application_name,
    pid.pid_alias as pid_alias,
    pid.channel_name as channel_name,
    pid.sub_channel_name as sub_channel_name,
    pid.medium_name as medium,
    pid.charge_rule as charge_rule,
    game.game_category_name as game_category_name,
    pid.pid_alias as batch,
    pid.pid_alias as account
from
    bi_gameop.dws_publish_pay_analy m
        left join bi_gameop.dim_pid pid on UPPER(m.pid) = UPPER(pid.pid)
        left join bi_gameop.yilewan_dim_game_info game on m.game_code = game.game_code;


drop table bi_gameop.dws_publish_dev_dis_view on cluster cluster_1shards_2replicas sync;
/**
 * 机型分析视图
 */
create view bi_gameop.dws_publish_dev_dis_view on cluster cluster_1shards_2replicas as
select
    partition_date,
    m.business as business,
    m.pid as pid,
    m.game_code as game_code,
    game_server,
    os,
    os_version,
    brand,
    model,
    dev_arr,
    pid.username as create_username,
    game.game_name as game_name,
    pid.department_code as department_code,
    pid.company_name as company_name,
    pid.application_name as application_name,
    pid.pid_alias as pid_alias,
    pid.channel_name as channel_name,
    pid.sub_channel_name as sub_channel_name,
    pid.medium_name as medium,
    pid.charge_rule as charge_rule,
    game.game_category_name as game_category_name,
    pid.pid_alias as batch,
    pid.pid_alias as account
from
    bi_gameop.dws_publish_dev_dis m
        left join bi_gameop.dim_pid pid on UPPER(m.pid) = UPPER(pid.pid)
        left join bi_gameop.yilewan_dim_game_info game on m.game_code = game.game_code;


drop table bi_gameop.dws_publish_user_login_view on cluster cluster_1shards_2replicas sync;
/**
 * 用户登录视图
 */
create view bi_gameop.dws_publish_user_login_view on cluster cluster_1shards_2replicas as
select
    partition_date,
    m.business as business,
    m.pid as pid,
    m.game_code as game_code,
    game_server,
    uid,
    arrayUniq(login_arr) as login_num,
    pid.username as create_username,
    game.game_name as game_name,
    pid.department_code as department_code,
    pid.company_name as company_name,
    pid.application_name as application_name,
    pid.pid_alias as pid_alias,
    pid.channel_name as channel_name,
    pid.sub_channel_name as sub_channel_name,
    pid.medium_name as medium,
    pid.charge_rule as charge_rule,
    game.game_category_name as game_category_name,
    pid.pid_alias as batch,
    pid.pid_alias as account
from
    bi_gameop.dws_publish_user_login m
        left join bi_gameop.dim_pid pid on UPPER(m.pid) = UPPER(pid.pid)
        left join bi_gameop.yilewan_dim_game_info game on m.game_code = game.game_code;


/**
   * ==============================================分析
   */
drop table bi_gameop.dim_pid on cluster cluster_1shards_2replicas;
CREATE TABLE bi_gameop.dim_pid on cluster cluster_1shards_2replicas
(
    `pid`                   String,
    `pid_alias`             String,
    `first_level_business`  String,
    `second_level_business` String,
    `third_level_business`  String,
    `company_id`            UInt64,
    `company_name`          String,
    `channel_id`            UInt64,
    `channel_name`          String,
    `sub_channel_id`        String,
    `sub_channel_name`      String,
    `pp_id`                 UInt64,
    `pp_name`               String,
    `charge_rule`           String,
    `product_code`          String,
    `product_name`          String,
    `application_id`        String,
    `application_name`      String,
    `medium_id`             String,
    `medium_name`           String,
    `userid`                UInt64,
    `username`              String,
    `department_code`       String,
    `department_name`       String
)
    ENGINE = MySQL('olapdb03:33066',
                   'bi_data_management',
                   'dm_channel_promotion_all_view',
                   'bi_dm_user',
                   'ylstJOkMcGvDfNY9');


/**
 * PID消耗视图
 */
create table if not exists  bi_gameop.dim_pid_cost on cluster cluster_1shards_2replicas
(
    `id`           UInt64,
    `cost_date`    Date,
    `pid`          String,
    `charge_model` String,
    `source`       String,
    `book_cost`    Float64,
    `real_cost`    Float64
) ENGINE = MySQL('db-master-bi-dev.stnts.com:33066',
                 'bi_pubop',
                 'dim_cost',
                 'bi_pubop',
                 'kgEIvoXcLv9tOPe3');


drop table bi_gameop.dws_publish_global_agg_view on cluster cluster_1shards_2replicas sync;
/**
  * 发行主视图
  */
create view bi_gameop.dws_publish_global_agg_view on cluster cluster_1shards_2replicas as
select dws.dim_type as dim_type,
       partition_date,
       partition_hour,
       partition_minute,
       dws.business as business,
       dws.pid as pid,
       dws.game_code as game_code,
       game_server,
       case when dws.is_a = 1 then 1 when dws.game_code = pid.product_code then 1 else dws.is_a end as is_a,
       province,
       reg_arr,
       login_new_arr,
       new_arr,
       unreal_new_arr,
       active_arr,
       valid_new_arr,
       unreal_valid_new_arr,
       create_role_arr,
       create_role_new_arr,
       roll_login_arr,
       pay_total_unit_arr,
       pay_total_num,
       pay_total_money,
       pay_total_e,
       pay_total_coupon,
       pay_total_rmb,
       pay_new_unit_arr,
       pay_new_num,
       pay_new_money,
       pay_first_unit_arr,
       pay_first_num,
       pay_first_money,
       pid.username as create_username,
       game.game_name as game_name,
       pid.department_code as department_code,
       pid.company_name as company_name,
       pid.application_name as application_name,
       pid.pid_alias as pid_alias,
       pid.channel_name as channel_name,
       pid.sub_channel_name as sub_channel_name,
       pid.medium_name as medium,
       pid.charge_rule as charge_rule,
       game.game_category_name as game_category_name,
       pid.pid_alias as batch,
       pid.pid_alias as account,
       cost.charge_model as charge_model,
       cost.`source` as traffic_source,
       cost.book_cost book_cost,
       cost.real_cost real_cost
from bi_gameop.dws_publish_global_agg dws
         left join bi_gameop.dim_pid pid on UPPER(dws.pid) = UPPER(pid.pid)
         left join bi_gameop.yilewan_dim_game_info game on dws.game_code = game.game_code
         left join bi_gameop.dim_pid_cost cost on dws.pid = cost.pid and dws.partition_date = cost.cost_date;



drop table bi_gameop.dw_publish_retention_agg_view on cluster cluster_1shards_2replicas sync;
/**
* 发行留存日视图
*/
create view bi_gameop.dw_publish_retention_agg_view on cluster cluster_1shards_2replicas as
select ret_date ,
       new_date ,
       date_gap ,
       dim_type ,
       ret.business as business ,
       ret.game_code as game_code,
       ret.pid as pid,
       ret.game_server as game_server ,
       ret.new_arr as new_arr ,
       ret.pay_arr as pay_arr ,
       ret.new_pay_arr as new_pay_arr,
       ret.active_arr as active_arr ,
       ret.pay_money as pay_money ,
       pid.username as create_username,
       game.game_name as game_name,
       pid.department_code as department_code,
       pid.company_name as company_name,
       pid.application_name as application_name,
       pid.pid_alias as pid_alias,
       pid.channel_name as channel_name,
       pid.sub_channel_name as sub_channel_name,
       pid.medium_name as medium,
       pid.charge_rule as charge_rule,
       game.game_category_name as game_category_name,
       pid.pid_alias as batch,
       pid.pid_alias as account
from bi_gameop.dw_publish_retention_agg ret
         left join bi_gameop.dim_pid pid on UPPER(ret.pid) = UPPER(pid.pid)
         left join bi_gameop.yilewan_dim_game_info game on ret.game_code = game.game_code;

drop table bi_gameop.dw_publish_retention_agg_week_view on cluster cluster_1shards_2replicas sync;
/**
 * 留存周视图
 */
create view bi_gameop.dw_publish_retention_agg_week_view on cluster cluster_1shards_2replicas as
select ret_date ,
       new_date ,
       date_gap ,
       dim_type ,
       ret.business as business ,
       ret.game_code as game_code,
       ret.pid as pid,
       ret.game_server as game_server ,
       ret.new_arr as new_arr ,
       ret.pay_arr as pay_arr ,
       ret.new_pay_arr as new_pay_arr,
       ret.active_arr as active_arr ,
       ret.pay_money as pay_money ,
       pid.username as create_username,
       game.game_name as game_name,
       pid.department_code as department_code,
       pid.company_name as company_name,
       pid.application_name as application_name,
       pid.pid_alias as pid_alias,
       pid.channel_name as channel_name,
       pid.sub_channel_name as sub_channel_name,
       pid.medium_name as medium,
       pid.charge_rule as charge_rule,
       game.game_category_name as game_category_name,
       pid.pid_alias as batch,
       pid.pid_alias as account
from bi_gameop.dw_publish_retention_agg_week ret
         left join bi_gameop.dim_pid pid on UPPER(ret.pid) = UPPER(pid.pid)
         left join bi_gameop.yilewan_dim_game_info game on ret.game_code = game.game_code;


drop table bi_gameop.dw_publish_retention_agg_month_view on cluster cluster_1shards_2replicas sync;
/**
 * 留存月视图
 */
create view bi_gameop.dw_publish_retention_agg_month_view on cluster cluster_1shards_2replicas as
select ret_date ,
       new_date ,
       date_gap ,
       dim_type ,
       ret.business as business ,
       ret.game_code as game_code,
       ret.pid ,
       ret.game_server as game_server ,
       ret.new_arr as new_arr ,
       ret.pay_arr as pay_arr ,
       ret.new_pay_arr as new_pay_arr ,
       ret.active_arr as active_arr ,
       ret.pay_money as pay_money ,
       pid.username as create_username,
       game.game_name as game_name,
       pid.department_code as department_code,
       pid.company_name as company_name,
       pid.application_name as application_name,
       pid.pid_alias as pid_alias,
       pid.channel_name as channel_name,
       pid.sub_channel_name as sub_channel_name,
       pid.medium_name as medium,
       pid.charge_rule as charge_rule,
       game.game_category_name as game_category_name,
       pid.pid_alias as batch,
       pid.pid_alias as account
from bi_gameop.dw_publish_retention_agg ret
         left join bi_gameop.dim_pid pid on UPPER(ret.pid) = UPPER(pid.pid)
         left join bi_gameop.yilewan_dim_game_info game on ret.game_code = game.game_code;


truncate table bi_gameop.dw_publish_retention_pay_his_agg on cluster cluster_1shards_2replicas sync;
/**
 * 将付费用户迁移到此处  付费用户的历史表
 */
create table if not exists  bi_gameop.dw_publish_retention_pay_his_agg on cluster cluster_1shards_2replicas
(
    `ret_date`      Date comment '留存日期',
    `dim_type`      String comment '统计类型：user.用户；dev.设备；role.角色',
    `business`      String comment 'business',
    `game_code`     String comment '游戏CODE',
    `pid`           String comment 'PID',
    `game_server`   String comment '区服ID',
    `pay_arr`       SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '付费',
    `new_pay_arr`   SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费'
    ) ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dw_publish_retention_pay_his_agg',
                                              '{replica}')
    PARTITION BY (ret_date)
    ORDER BY
(dim_type, business, game_code, pid, game_server) SETTINGS index_granularity = 8192;

drop table bi_gameop.dw_publish_retention_pay_his_2agg on cluster cluster_1shards_2replicas sync;
/**
 * 将留存中付费 提取到
 */
CREATE MATERIALIZED VIEW bi_gameop.dw_publish_retention_pay_his_2agg
on cluster cluster_1shards_2replicas
to bi_gameop.dw_publish_retention_pay_his_agg
as
select ret_date,
       dim_type,
       business,
       game_code,
       pid,
       game_server,
       groupUniqArrayArray(pay_arr)       as pay_arr,
       groupUniqArrayArray(new_pay_arr)   as new_pay_arr
from bi_gameop.dw_publish_retention_agg ret where ret.date_gap = 0
group by ret_date, date_gap, dim_type, business, game_code, pid, game_server
HAVING notEmpty(pay_arr) = 1 ;



/**
 * 付费留存
 */
create table if not exists  bi_gameop.dw_publish_retention_pay_agg on cluster cluster_1shards_2replicas
(
    `ret_date`    Date comment '留存日期',
    `new_date`    Date comment '新增日期',
    `date_gap`    UInt8 comment '间距',
    `dim_type`    String comment '统计类型：user.用户；dev.设备；role.角色',
    `business`    String comment 'business',
    `game_code`   String comment '游戏CODE',
    `pid`         String comment 'PID',
    `game_server` String comment '区服ID',
    `pay_arr`     SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '付费用户',
    `new_pay_arr` SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费用户',
    `active_arr`  SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '付费活跃用户',
    `new_active_arr`  SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费活跃用户'
    ) ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dw_publish_retention_pay_agg',
                                              '{replica}')
    PARTITION BY (ret_date)
    ORDER BY
(dim_type, date_gap, business, game_code, pid, game_server) SETTINGS index_granularity = 8192;

/**
 * 新增付费留存
 */
--create table if not exists  bi_gameop.dw_publish_retention_pay_new_agg on cluster cluster_1shards_2replicas
    --(
--    `ret_date`    Date comment '留存日期',
--    `new_date`    Date comment '新增日期',
--    `date_gap`    UInt8 comment '间距',
--    `dim_type`    String comment '统计类型：user.用户；dev.设备；role.角色',
--    `business`    String comment 'business',
--    `game_code`   String comment '游戏CODE',
--    `pid`         String comment 'PID',
--    `game_server` String comment '区服ID',
--    `pay_arr`     SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费用户',
--    `active_arr` SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '活跃用户'
    --) ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dw_publish_retention_pay_agg',
--           '{replica}')
--      PARTITION BY (ret_date)
--      ORDER BY
--          (dim_type, date_gap, business, game_code, pid, game_server) SETTINGS index_granularity = 8192;

/**
 * 物化视图写入付费用户留存
 */
                                                CREATE MATERIALIZED VIEW bi_gameop.dw_publish_retention_pay_2agg
                                                on cluster cluster_1shards_2replicas
                                                to bi_gameop.dw_publish_retention_pay_agg
    as
                                                select
                                                t_active.ret_date,
                                                t_pay.ret_date as new_date,
                                                dateDiff('day', t_pay.ret_date, t_active.ret_date) as date_gap,
    t_active.dim_type,
    t_active.business,
    t_active.game_code,
    t_active.pid,
    t_active.game_server,
    t_pay.pay_arr as pay_arr,
    t_pay.new_pay_arr as new_pay_arr,
    arrayIntersect(t_active.active_arr, t_pay.pay_arr) as active_arr,
    arrayIntersect(t_active.active_arr, t_pay.new_pay_arr) as new_active_arr
    from
  (
      select
      ret_date,
      dim_type,
      business,
      game_code,
      pid,
      game_server,
      groupUniqArrayArray(active_arr) as active_arr
    from
    bi_gameop.dw_publish_retention_agg ret
    where
    date_gap = 0
    group by
    ret_date,
    dim_type,
    business,
    game_code,
    pid,
    game_server
    ) t_active
    join (
             select
             ret_date,
             dim_type,
             business,
             game_code,
             pid,
             game_server,
             groupUniqArrayArray(pay_arr) as pay_arr,
    groupUniqArrayArray(new_pay_arr) as new_pay_arr
    from
    bi_gameop.dw_publish_retention_pay_his_agg ret
    where
    ret_date >= addDays(ret_date, -180)
    group by
    ret_date,
    dim_type,
    business,
    game_code,
    pid,
    game_server
    ) t_pay on t_active.dim_type = t_pay.dim_type
    and t_active.business = t_pay.business
    and t_active.game_code = t_pay.game_code
    and t_active.pid = t_pay.pid
    and t_active.game_server = t_pay.game_server;

/**
 * 周付费留存
 */
    create table if not exists  bi_gameop.dw_publish_retention_pay_agg_week on cluster cluster_1shards_2replicas
  (
      `ret_date`    Date comment '留存日期',
      `new_date`    Date comment '新增日期',
      `date_gap`    UInt8 comment '间距',
      `dim_type`    String comment '统计类型：user.用户；dev.设备；role.角色',
      `business`    String comment 'business',
      `game_code`   String comment '游戏CODE',
      `pid`         String comment 'PID',
      `game_server` String comment '区服ID',
      `pay_arr`     SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '付费用户',
    `new_pay_arr` SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费用户',
    `active_arr`  SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '付费活跃用户',
    `new_active_arr`  SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费活跃用户'
    ) ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dw_publish_retention_pay_agg_week',
                                              '{replica}')
    PARTITION BY (ret_date)
    ORDER BY
  (dim_type, date_gap, business, game_code, pid, game_server) SETTINGS index_granularity = 8192;

    CREATE MATERIALIZED VIEW bi_gameop.dw_publish_retention_pay_2agg_week_mv
                                                                         on cluster cluster_1shards_2replicas
    to bi_gameop.dw_publish_retention_pay_agg_week
    as
    select toStartOfWeek(ret_date, 1) as ret_date,
    toStartOfWeek(new_date, 1) as new_date,
    dateDiff('week', toStartOfWeek(new_date, 1), toStartOfWeek(ret_date, 1)) as date_gap,
    dim_type,
    business,
    game_code,
    pid,
    game_server,
    groupUniqArrayArray(pay_arr)         as pay_arr,
    groupUniqArrayArray(new_pay_arr)     as new_pay_arr,
    groupUniqArrayArray(active_arr)      as active_arr,
    groupUniqArrayArray(new_active_arr)  as new_active_arr
    from bi_gameop.dw_publish_retention_pay_agg as ret
    group by toStartOfWeek(ret.ret_date, 1), toStartOfWeek(ret.new_date, 1), dim_type, business, game_code, pid, game_server ;



/**
 * 月付费留存
 */
    create table if not exists  bi_gameop.dw_publish_retention_pay_agg_month on cluster cluster_1shards_2replicas
  (
      `ret_date`    Date comment '留存日期',
      `new_date`    Date comment '新增日期',
      `date_gap`    UInt8 comment '间距',
      `dim_type`    String comment '统计类型：user.用户；dev.设备；role.角色',
      `business`    String comment 'business',
      `game_code`   String comment '游戏CODE',
      `pid`         String comment 'PID',
      `game_server` String comment '区服ID',
      `pay_arr`     SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '付费用户',
    `new_pay_arr` SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费用户',
    `active_arr`  SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '付费活跃用户',
    `new_active_arr`  SimpleAggregateFunction(groupUniqArrayArray, Array (String)) comment '新增付费活跃用户'
    ) ENGINE = ReplicatedAggregatingMergeTree('/clickhouse/tables/{layer}-{shard02}/bi_gameop/dw_publish_retention_pay_agg_month',
                                              '{replica}')
    PARTITION BY (ret_date)
    ORDER BY
  (dim_type, date_gap, business, game_code, pid, game_server) SETTINGS index_granularity = 8192;

    CREATE MATERIALIZED VIEW bi_gameop.dw_publish_retention_pay_2agg_month_mv
                                                                         on cluster cluster_1shards_2replicas
    to bi_gameop.dw_publish_retention_pay_agg_month
    as
    select toStartOfMonth(ret_date) as ret_date,
    toStartOfMonth(new_date) as new_date,
    dateDiff('month', toStartOfMonth(new_date), toStartOfMonth(ret_date)) as date_gap,
    dim_type,
    business,
    game_code,
    pid,
    game_server,
    groupUniqArrayArray(pay_arr)         as pay_arr,
    groupUniqArrayArray(new_pay_arr)     as new_pay_arr,
    groupUniqArrayArray(active_arr)      as active_arr,
    groupUniqArrayArray(new_active_arr)  as new_active_arr
    from bi_gameop.dw_publish_retention_pay_agg as ret
    group by toStartOfMonth(ret.ret_date), toStartOfMonth(ret.new_date), dim_type, business, game_code, pid, game_server ;


    drop table bi_gameop.dw_publish_retention_pay_agg_view on cluster cluster_1shards_2replicas sync;

    create view bi_gameop.dw_publish_retention_pay_agg_view on cluster cluster_1shards_2replicas as
    select ret_date ,
    new_date ,
    date_gap ,
    dim_type ,
    ret.business as business ,
    ret.game_code as game_code,
    ret.pid as pid,
    ret.game_server as game_server ,
    ret.pay_arr as pay_arr ,
    ret.new_pay_arr as new_pay_arr ,
    ret.active_arr as active_arr ,
    ret.new_active_arr as new_active_arr ,
    pid.username as create_username,
    game.game_name as game_name,
    pid.department_code as department_code,
    pid.company_name as company_name,
    pid.application_name as application_name,
    pid.pid_alias as pid_alias,
    pid.channel_name as channel_name,
    pid.sub_channel_name as sub_channel_name,
    pid.medium_name as medium,
    pid.charge_rule as charge_rule,
    game.game_category_name as game_category_name,
    pid.pid_alias as batch,
    pid.pid_alias as account
    from bi_gameop.dw_publish_retention_pay_agg ret
    left join bi_gameop.dim_pid pid on UPPER(ret.pid) = UPPER(pid.pid)
    left join bi_gameop.yilewan_dim_game_info game on ret.game_code = game.game_code;


    create view bi_gameop.dw_publish_retention_pay_agg_week_view on cluster cluster_1shards_2replicas as
    select ret_date ,
    new_date ,
    date_gap ,
    dim_type ,
    ret.business as business ,
    ret.game_code as game_code,
    ret.pid ,
    ret.game_server as game_server ,
    ret.pay_arr as pay_arr ,
    ret.new_pay_arr as new_pay_arr ,
    ret.active_arr as active_arr ,
    ret.new_active_arr as new_active_arr ,
    pid.username as create_username,
    game.game_name as game_name,
    pid.department_code as department_code,
    pid.company_name as company_name,
    pid.application_name as application_name,
    pid.pid_alias as pid_alias,
    pid.channel_name as channel_name,
    pid.sub_channel_name as sub_channel_name,
    pid.medium_name as medium,
    pid.charge_rule as charge_rule,
    game.game_category_name as game_category_name,
    pid.pid_alias as batch,
    pid.pid_alias as account
    from bi_gameop.dw_publish_retention_pay_agg_week ret
    left join bi_gameop.dim_pid pid on UPPER(ret.pid) = UPPER(pid.pid)
    left join bi_gameop.yilewan_dim_game_info game on ret.game_code = game.game_code;



    create view bi_gameop.dw_publish_retention_pay_agg_month_view on cluster cluster_1shards_2replicas as
    select ret_date ,
    new_date ,
    date_gap ,
    dim_type ,
    ret.business as business ,
    ret.game_code as game_code,
    ret.pid ,
    ret.game_server as game_server ,
    ret.pay_arr as pay_arr ,
    ret.new_pay_arr as new_pay_arr ,
    ret.active_arr as active_arr ,
    ret.new_active_arr as new_active_arr ,
    pid.username as create_username,
    game.game_name as game_name,
    pid.department_code as department_code,
    pid.company_name as company_name,
    pid.application_name as application_name,
    pid.pid_alias as pid_alias,
    pid.channel_name as channel_name,
    pid.sub_channel_name as sub_channel_name,
    pid.medium_name as medium,
    pid.charge_rule as charge_rule,
    game.game_category_name as game_category_name,
    pid.pid_alias as batch,
    pid.pid_alias as account
    from bi_gameop.dw_publish_retention_pay_agg_month ret
    left join bi_gameop.dim_pid pid on UPPER(ret.pid) = UPPER(pid.pid)
    left join bi_gameop.yilewan_dim_game_info game on ret.game_code = game.game_code;

/**
 * 付费用户留存
 */
    select t_active.ret_date, t_pay.ret_date, dateDiff('day', t_pay.ret_date, t_active.ret_date) as date_gap,t_active.dim_type , t_active.business ,
    t_active.game_code , t_active.pid , t_active.game_server, t_active.pay_arr as pay_arr , t_active.active_arr, arrayIntersect(t_active.active_arr, t_pay.pay_arr) as ret_arr from (
                                                                                                                                                                                        select ret_date, dim_type , business , game_code , pid , game_server, groupUniqArrayArray(active_arr) as active_arr, groupUniqArrayArray(pay_arr) as pay_arr
    from bi_gameop.dw_publish_retention_agg ret
    where date_gap = 0 and ret_date  = '2021-09-01'
    group by ret_date, dim_type , business , game_code , pid , game_server ) t_active
    join
  (
      select ret_date, dim_type , business , game_code , pid , game_server, groupUniqArrayArray(pay_arr) as pay_arr
    from bi_gameop.dw_publish_retention_agg ret
    where date_gap = 0 and ret_date  >= addDays(ret_date, -180)
    group by ret_date, dim_type , business , game_code , pid , game_server ) t_pay
                                                                         on t_active.dim_type = t_pay.dim_type and t_active.business = t_pay.business
    and t_active.game_code = t_pay.game_code and t_active.pid = t_pay.pid and t_active.game_server = t_pay.game_server;
