/**
  数据管理二期需求  兼  渠道管理功能
 */

alter table dm_business_dict add column root_level varchar(255) after id;
alter table dm_business_dict add column department_code varchar(100)  after id;

create table dm_channel
(
	channel_id bigint unsigned not null
		primary key,
	channel_name varchar(100) null,
	company_id bigint null comment '公司ID',
	company_name varchar(255) null comment '公司名称',
	department_code varchar(100) null comment '部门code',
	department_name varchar(255) null comment '部门名称',
	secret_type tinyint(1) default 2 null comment '保密类型1:共享2:私有',
	userid mediumtext not null comment '用户ID',
	username varchar(100) not null comment '用户名',
	create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
	update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
	constraint unique_channel_name
		unique (channel_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '渠道表';

create table dm_channel_child
(
	id bigint unsigned auto_increment comment '主键'
		primary key,
	ccid varchar(100) null comment 'CCID',
	channel_id bigint null,
	sub_channel_id bigint unsigned null comment '子渠道ID',
	sub_channel_name varchar(100) null comment '子渠道名称',
	create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
	update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
	constraint dm_channel_child_UN
		unique (sub_channel_id)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '子渠道';

create table dm_channel_cooperation
(
	id bigint unsigned auto_increment comment '主键'
		primary key,
	ccid varchar(100) null comment 'CCID',
	agent_id bigint unsigned null comment '供应商id',
	agent_name varchar(100) null comment '供应商名称',
	channel_id bigint unsigned null comment '渠道ID',
	channel_name varchar(100) null comment '渠道名称',
	department_name varchar(255) null comment '部门名称',
	department_code varchar(255) null comment '部门code',
	first_level_business varchar(100) null comment '一级分类',
	second_level_business varchar(100) null comment '二级分类',
	third_level_business varchar(100) null comment '三级分类',
	charge_rule varchar(100) null comment '计费规则',
	channel_rate decimal(10,4) null comment '渠道费率',
	channel_share decimal(10,4) null comment '渠道分成',
	channel_share_step varchar(1000) null comment '渠道阶梯分成',
	price decimal(10,2) null comment '单价',
	userid bigint unsigned null comment '负责人id',
	username varchar(100) null comment '负责人名称',
	create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
	update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间',
	constraint dm_channel_cooperation_UN
		unique (ccid)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '渠道合作';

create table dm_channel_medium
(
	id bigint unsigned auto_increment comment '主键'
		primary key,
	department_code varchar(100) null,
	department_name varchar(255) null,
	name varchar(100) null comment '名称',
	description varchar(255) null comment '描述',
	userid bigint unsigned null comment '修改人id',
	username varchar(100) null comment '修改人名称',
	create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	constraint dm_channel_medium__unique
		unique (department_code, name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '媒介信息';

create table dm_channel_product
(
	id bigint unsigned auto_increment comment '主键'
		primary key,
	department_code varchar(100) null,
	department_name varchar(255) null,
	cooperation_main_id bigint unsigned null comment '广告主id',
	cooperation_main_name varchar(50) null comment '广告主名称',
	product_id bigint unsigned null comment '产品id',
	product_name varchar(100) null comment '产品名称',
	product_code varchar(100) null comment '产品code',
	application_id bigint unsigned null comment '应用id',
	application_name varchar(100) null comment '应用',
	userid bigint unsigned null comment '负责人id',
	username varchar(100) null comment '负责人名称',
	create_time datetime default CURRENT_TIMESTAMP not null comment '创建时间',
	update_time datetime default CURRENT_TIMESTAMP not null on update CURRENT_TIMESTAMP comment '更新时间',
	constraint dm_channel_product_unique
		unique (product_code, application_name)
)ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '产品信息';

create table dm_channel_promotion
(
	id bigint unsigned auto_increment comment '主键'
		primary key,
	pid varchar(100) null comment 'PID',
	pid_alias varchar(50) null comment 'PID别名',
	ccid varchar(100) null comment 'CCID',
	sub_channel_id bigint unsigned null comment '子渠道id',
	pp_id bigint null comment '推广位ID',
	product_id bigint unsigned null comment '产品id',
	application_id bigint unsigned null comment '应用id',
	medium_id varchar(100) null comment '推广媒介id',
	extra varchar(100) null comment '拓展字段',
	userid bigint unsigned null comment '负责人id',
	username varchar(100) null comment '负责人名称',
	create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
	update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '渠道推广';

create table dm_channel_promotion_position
(
	pp_id bigint unsigned auto_increment
		primary key,
	channel_id bigint null comment '渠道ID',
	pp_name varchar(255) null comment '推广位名称',
	pp_status tinyint(1) null comment '推广位状态1：启动0：停用',
	create_time datetime default CURRENT_TIMESTAMP null comment '创建时间',
	update_time datetime default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP comment '更新时间'
)ENGINE=InnoDB DEFAULT CHARSET=utf8 comment '推广位表';


alter table dm_business_check modify third_level_business varchar(255) default '-' null comment '三级分类';
alter table dm_business_dict modify third_level varchar(255) default '-' null comment '三级分类';

-- 修改唯一性
alter table dm_channel_product drop key dm_channel_product_unique;
alter table dm_channel_product
    add constraint dm_channel_product_unique
        unique (department_code, product_code, application_name);

drop index dm_channel_child_UN on dm_channel_child;
create unique index dm_channel_child_UN
    on dm_channel_child (channel_id, sub_channel_name);

create unique index dm_channel_promotion_position_unique
    on dm_channel_promotion_position (channel_id, pp_name);



