/**
by:liang.zhang
date:2020-03-24
version:1.0
*/

DROP DATABASE IF EXISTS stbi;
CREATE DATABASE stbi DEFAULT CHARACTER SET utf8;
USE stbi;

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

#指标定义表
DROP TABLE IF EXISTS `stbi_kpi_desc`;
CREATE TABLE `stbi_kpi_desc`(
  `kpi_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '指标维护主键',
  `kpi_key` VARCHAR(255) NOT NULL COMMENT '指标KEY',
  `kpi_name` VARCHAR(200) NOT NULL COMMENT '指标名称',
  `kpi_desc` VARCHAR(10000) NOT NULL COMMENT '指标描述',
  `kpi_comment` VARCHAR(200) COMMENT '备注',
  `created_by` INT(11) NOT NULL COMMENT '创建人ID',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY(`kpi_id`),
  UNIQUE INDEX `kpi_key`(`kpi_key`)
)ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COMMENT = '指标管理';

#用户表
DROP TABLE IF EXISTS `stbi_user`;
CREATE TABLE `stbi_user`(
  `user_id` INT(11) UNSIGNED NOT NULL COMMENT '用户ID',
  `cnname` VARCHAR(100) COMMENT '用户名',
  `oa_status` TINYINT(1) COMMENT '用户状态 状态：1 激活, 0 未激活',
  `mobile` VARCHAR(11) COMMENT '手机号码',
  `card_number` INT COMMENT '工号',
  `email` VARCHAR(100) COMMENT '邮箱',
  `person_id` INT COMMENT '员工号',
  `is_admin` TINYINT DEFAULT 0 COMMENT '是否超级管理员1:是,0:否',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '创建日期',
  PRIMARY KEY(`user_id`)
  --
)ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COMMENT = '用户表';

#角色表
DROP TABLE IF EXISTS `stbi_role`;
CREATE TABLE `stbi_role`(
  `role_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '角色ID',
  `role_name` VARCHAR(100) NOT NULL COMMENT '角色名称',
  `status` TINYINT(1) NULL DEFAULT 1 COMMENT '状态=>1:正常,9禁用',
  `role_desc` VARCHAR(500) COMMENT '角色描述',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY(`role_id`),
  UNIQUE INDEX `role_name`(`role_name`)
)ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COMMENT = '角色表';

#权限表
DROP TABLE IF EXISTS `stbi_perm`;
CREATE TABLE `stbi_perm`(
  `perm_id` INT(11) UNSIGNED NOT NULL COMMENT '权限ID',
  `perm_name` VARCHAR(200) NOT NULL COMMENT '权限名称',
  `parent_perm_id` INT(11) NOT NULL DEFAULT -1 COMMENT '父权限ID',
  `perm_code` VARCHAR(100) NOT NULL COMMENT '权限码',
  `perm_type` TINYINT(1) DEFAULT 1 COMMENT '权限类型：1.菜单,2.操作,3.数据(业务线)',
  `order_num` INT DEFAULT 0 COMMENT '排序',
  `perm_desc` VARCHAR(200) COMMENT '备注',
  `created_at` DATETIME NOT NULL COMMENT '创建时间',
  `updated_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '修改时间',
  PRIMARY KEY(`perm_id`),
  UNIQUE INDEX `perm_code`(`perm_code`)
)ENGINE = InnoDB CHARACTER SET = utf8 COMMENT = '权限表';  

DROP TABLE IF EXISTS `stbi_product`;
CREATE TABLE `stbi_product`(
  `product_id` INT(11) UNSIGNED NOT NULL COMMENT '产品线ID',
  `product_name` VARCHAR(200) NOT NULL COMMENT '产品线名称',
  `status` TINYINT DEFAULT 1 COMMENT '状态:1.启用,0.停用',
  PRIMARY KEY(`product_id`) 
)ENGINE = InnoDB CHARACTER SET = utf8 COMMENT = '产品线表';  

-- DROP TABLE IF EXISTS `stbi_perms`;
-- CREATE TABLE `stbi_perms`(
--   `perm_id` INT UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '权限ID',
--   `isTop` TINYINT(1) DEFAULT 0 COMMENT '是否顶层目录',
--   `menu_first` 
-- )

#用户角色表
-- DROP TABLE IF EXISTS `stbi_user_role`;
-- CREATE TABLE `stbi_user_role`(
--   `user_id` INT(11) NOT NULL COMMENT '用户ID',
--   `role_id` INT(11) NOT NULL COMMENT '角色ID'
-- )ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COMMENT = '用户角色表';

#用户角色表[带产品线]
DROP TABLE IF EXISTS `stbi_user_role`;
CREATE TABLE `stbi_user_role`(
  `user_id` INT(11) NOT NULL COMMENT '用户ID',
  `role_id` INT(11) NOT NULL COMMENT '角色ID',
  `product_ids` VARCHAR(200) COMMENT '产品线ID(多个产品线逗号分隔),eg:1,2,3',
  `product_names` VARCHAR(500) COMMENT '产品线名称(多个产品线逗号分隔,eg:云盘,带带)'
)ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COMMENT = '用户角色表[带产品线]';

#角色权限表
DROP TABLE IF EXISTS `stbi_role_perm`;
CREATE TABLE `stbi_role_perm`(
  `role_id` INT(11) NOT NULL COMMENT '权限ID',
  `perm_id` INT(11) NOT NULL COMMENT '权限ID'
)ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COMMENT = '角色权限表';

DROP TABLE IF EXISTS `stbi_log_op`;
CREATE TABLE `stbi_log_op`(
  `log_id` INT(11) UNSIGNED NOT NULL AUTO_INCREMENT COMMENT '日志主键',
  `req_url` VARCHAR(500) COMMENT '请求URL',
  `log_type` VARCHAR(200) DEFAULT 'view' COMMENT '操作类型:view.浏览,new.新增,modify.编辑,del.删除,export.导出',
  `log_ip` VARCHAR(50) COMMENT 'IP',
  `created_by` INT(11) NOT NULL COMMENT '操作人',
  `created_at` TIMESTAMP DEFAULT CURRENT_TIMESTAMP COMMENT '操作时间',
  PRIMARY KEY(`log_id`),
  INDEX `log_index`(`created_at`, `log_ip`)
)ENGINE = InnoDB AUTO_INCREMENT = 1 CHARACTER SET = utf8 COMMENT = '操作日志表';
