
DROP TABLE IF EXISTS `dm_business_check`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_business_check` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '业务考核ID',
  `department` varchar(255) NOT NULL COMMENT '部分',
  `department_code` varchar(255) DEFAULT NULL COMMENT '部门code',
  `first_level_business` varchar(255) NOT NULL COMMENT '一级分类',
  `second_level_business` varchar(255) NOT NULL COMMENT '二级分类',
  `third_level_business` varchar(255) NOT NULL COMMENT '三级分类',
  `check_level` varchar(255) NOT NULL COMMENT '考核级别',
  `check_target` varchar(255) NOT NULL COMMENT '考核指标',
  `check_target_Indicator` double NOT NULL COMMENT '考核目标',
  `check_start_date` date NOT NULL COMMENT '考核期限开始时间',
  `check_end_date` date NOT NULL COMMENT '考核期限结束时间',
  `validity_start_date` date DEFAULT NULL COMMENT '数据有效期开始时间',
  `validity_end_date` date DEFAULT NULL COMMENT '数据有效期开始时间',
  `create_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `update_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '更新日期',
  `is_valid` tinyint(3) unsigned DEFAULT '1' COMMENT '是否有效。0：否；1：是。',
  `is_delete` tinyint(3) unsigned DEFAULT '0' COMMENT '是否删除。0：否；1：是。',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=45 DEFAULT CHARSET=utf8 COMMENT='业务考核';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_business_check_history`
--

DROP TABLE IF EXISTS `dm_business_check_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_business_check_history` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `business_check_id` bigint(20) unsigned NOT NULL COMMENT '业务考核ID',
  `department` varchar(255) DEFAULT NULL COMMENT '部门',
  `source_content` varchar(1000) DEFAULT NULL COMMENT '变更前',
  `target_content` varchar(1000) DEFAULT NULL COMMENT '变更后',
  `opration` varchar(255) DEFAULT NULL COMMENT '操作',
  `create_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` varchar(255) DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=83 DEFAULT CHARSET=utf8 COMMENT='业务考核历史记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_business_dict`
--

DROP TABLE IF EXISTS `dm_business_dict`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_business_dict` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `first_level` varchar(255) NOT NULL COMMENT '一级分类',
  `second_level` varchar(255) NOT NULL COMMENT '二级分类',
  `third_level` varchar(255) NOT NULL COMMENT '三级分类',
  `is_valid` tinyint(3) unsigned DEFAULT '1' COMMENT '是否有效。0：否；1：是。',
  PRIMARY KEY (`id`),
  UNIQUE KEY `dm_business_dict_first_level_second_level_third_level_IDX` (`first_level`,`second_level`,`third_level`) USING BTREE
) ENGINE=InnoDB AUTO_INCREMENT=41 DEFAULT CHARSET=utf8 COMMENT='业务分类';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_cooperation`
--

DROP TABLE IF EXISTS `dm_cooperation`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_cooperation` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '合作方ID',
  `cooperation_type` tinyint(3) unsigned NOT NULL COMMENT '合作方类型。1=上游客户；2=下游供应商',
  `cp_name` varchar(255) DEFAULT NULL COMMENT '客户简称（公司简称）',
  `cp_code` varchar(255) DEFAULT NULL COMMENT '助记码（类似简称/英文缩写）',
  `company_name` varchar(255) NOT NULL COMMENT '公司全称（营业执照全称）',
  `company_type` tinyint(3) unsigned DEFAULT NULL COMMENT '公司合作类型.1=代理;2=直客',
  `company_taxkey` varchar(500) DEFAULT NULL COMMENT '税务登记号',
  `company_legal` varchar(255) DEFAULT NULL COMMENT '公司法人代表（营业执照）',
  `company_tel` varchar(255) DEFAULT NULL COMMENT '公司电话',
  `company_contact` varchar(255) DEFAULT NULL COMMENT '公司联系人（对接人）',
  `contact_phone` varchar(255) DEFAULT NULL COMMENT '公司联系人手机',
  `contact_mail` varchar(255) DEFAULT NULL COMMENT '联系邮箱',
  `contact_fax` varchar(255) DEFAULT NULL COMMENT '传真',
  `company_website` varchar(255) DEFAULT NULL COMMENT '公司网址（官网）',
  `company_address` varchar(500) DEFAULT NULL COMMENT '公司地址',
  `company_desc` varchar(1000) DEFAULT NULL COMMENT '公司简介',
  `company_products` varchar(1000) DEFAULT NULL COMMENT '主要产品说明',
  `company_size` tinyint(3) unsigned DEFAULT NULL COMMENT '1=50人以下，2=50-200人，3=200-1000人，4=1000人以上',
  `company_qualification` varchar(1000) DEFAULT NULL COMMENT '公司资质（上传的一些资质证明）',
  `parent_industry` varchar(255) DEFAULT NULL COMMENT '所属行业（一级）',
  `child_industry` varchar(255) DEFAULT NULL COMMENT '所属行业（二级）',
  `eas_code` varchar(255) DEFAULT NULL COMMENT 'eas合作方编码',
  `is_approved` tinyint(3) unsigned DEFAULT NULL COMMENT '核准状态（财务审查核实）',
  `is_self` tinyint(3) unsigned DEFAULT NULL COMMENT '是否集团内公司',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` varchar(255) DEFAULT NULL COMMENT '创建人id（公司员工）',
  `create_user` varchar(255) DEFAULT NULL COMMENT '创建人（公司员工）',
  `create_department` varchar(255) DEFAULT NULL COMMENT '创建人所属部门',
  `create_department_code` varchar(255) DEFAULT NULL COMMENT '创建人所属部门编码',
  `handler_user` varchar(255) DEFAULT NULL COMMENT '经手人（公司员工）',
  `is_protection` tinyint(3) unsigned DEFAULT '0' COMMENT '是否启用隐私保护和保护级别.0=不保护（全公司开放，默认）；1=部门保护（部门可见）；2=私有保护（仅自己可见）',
  `is_test` tinyint(3) unsigned DEFAULT '0' COMMENT '是否测试使用。0=不是（默认），1=是',
  `qualification` varchar(255) DEFAULT NULL COMMENT '公司资质',
  `last_status` tinyint(3) unsigned DEFAULT '1' COMMENT '当前跟进状态。合作状态：1=启用（默认），2=停用（暂停合作）',
  `last_remark` varchar(500) DEFAULT NULL COMMENT '最新跟进记录',
  `data_source` tinyint(3) unsigned NOT NULL COMMENT '数据来源标识',
  `cp_level` tinyint(3) unsigned DEFAULT NULL COMMENT '客户等级',
  `cp_detail` varchar(1000) DEFAULT NULL COMMENT '客户的其他详细信息,Json结构',
  `related_products` varchar(500) DEFAULT NULL COMMENT '客户关联产品信息',
  `last_order_time` datetime DEFAULT NULL COMMENT '最后订单时间',
  `is_relate_contract` tinyint(3) unsigned DEFAULT '0' COMMENT '是否关联合同',
  `contract_id_set` text COMMENT '关联合同ID（集合）',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '客户信息更新时间',
  `update_version` int(10) unsigned DEFAULT NULL COMMENT '信息更新版本记录号',
  `handler_user_name` varchar(255) DEFAULT NULL COMMENT '经手人姓名（公司员工）',
  `company_contact_name` varchar(255) DEFAULT NULL COMMENT '公司联系人姓名（对接人）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11242 DEFAULT CHARSET=utf8 COMMENT='合作伙伴汇总表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_cooperation_bi`
--

DROP TABLE IF EXISTS `dm_cooperation_bi`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_cooperation_bi` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '合作方ID',
  `cooperation_type` tinyint(3) unsigned NOT NULL COMMENT '合作方类型。1=上游客户；2=下游供应商',
  `cp_name` varchar(255) DEFAULT NULL COMMENT '客户简称（公司简称）',
  `cp_code` varchar(255) DEFAULT NULL COMMENT '助记码（类似简称/英文缩写）',
  `company_name` varchar(255) NOT NULL COMMENT '公司全称（营业执照全称）',
  `company_type` tinyint(3) unsigned DEFAULT NULL COMMENT '公司合作类型.1=代理;2=直客',
  `company_taxkey` varchar(500) DEFAULT NULL COMMENT '税务登记号',
  `company_legal` varchar(255) DEFAULT NULL COMMENT '公司法人代表（营业执照）',
  `company_tel` varchar(255) DEFAULT NULL COMMENT '公司电话',
  `company_contact` varchar(255) DEFAULT NULL COMMENT '公司联系人（对接人）',
  `contact_phone` varchar(255) DEFAULT NULL COMMENT '公司联系人手机',
  `contact_mail` varchar(255) DEFAULT NULL COMMENT '联系邮箱',
  `contact_fax` varchar(255) DEFAULT NULL COMMENT '传真',
  `company_website` varchar(255) DEFAULT NULL COMMENT '公司网址（官网）',
  `company_address` varchar(500) DEFAULT NULL COMMENT '公司地址',
  `company_desc` varchar(1000) DEFAULT NULL COMMENT '公司简介',
  `company_products` varchar(1000) DEFAULT NULL COMMENT '主要产品说明',
  `company_size` tinyint(3) unsigned DEFAULT NULL COMMENT '1=50人以下，2=50-200人，3=200-1000人，4=1000人以上',
  `company_qualification` varchar(1000) DEFAULT NULL COMMENT '公司资质（上传的一些资质证明）',
  `parent_industry` varchar(255) DEFAULT NULL COMMENT '所属行业（一级）',
  `child_industry` varchar(255) DEFAULT NULL COMMENT '所属行业（二级）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` varchar(255) DEFAULT NULL COMMENT '创建人id（公司员工）',
  `create_user` varchar(255) DEFAULT NULL COMMENT '创建人（公司员工）',
  `create_department` varchar(255) DEFAULT NULL COMMENT '创建人所属部门',
  `create_department_code` varchar(255) DEFAULT NULL COMMENT '创建人所属部门编码',
  `handler_user` varchar(255) DEFAULT NULL COMMENT '经手人（公司员工）',
  `is_protection` tinyint(3) unsigned DEFAULT '0' COMMENT '是否启用隐私保护和保护级别.0=不保护（全公司开放，默认）；1=部门保护（部门可见）；2=私有保护（仅自己可见）',
  `is_test` tinyint(3) unsigned DEFAULT '0' COMMENT '是否测试使用。0=不是（默认），1=是',
  `last_status` tinyint(3) unsigned DEFAULT '1' COMMENT '当前跟进状态。合作状态：1=启用（默认），2=停用（暂停合作）',
  `last_remark` varchar(500) DEFAULT NULL COMMENT '最新跟进记录',
  `data_source` tinyint(3) unsigned NOT NULL COMMENT '数据来源标识',
  `related_products` varchar(500) DEFAULT NULL COMMENT '客户关联产品信息',
  `last_order_time` datetime DEFAULT NULL COMMENT '最后订单时间',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '客户信息更新时间',
  `update_version` int(10) unsigned DEFAULT NULL COMMENT '信息更新版本记录号',
  `handler_user_name` varchar(255) DEFAULT NULL COMMENT '经手人姓名（公司员工）',
  `company_contact_name` varchar(255) DEFAULT NULL COMMENT '公司联系人姓名（对接人）',
  `related_cooperation_id` bigint(20) unsigned DEFAULT NULL COMMENT '关联汇总表（dm_cooperation）的id',
  `update_user` varchar(255) DEFAULT NULL COMMENT '更新者',
  `update_user_id` varchar(255) DEFAULT NULL COMMENT '更新人id（公司员工）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=11241 DEFAULT CHARSET=utf8 COMMENT='合作伙伴 源表（BI平台+订单系统）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_cooperation_bi_history`
--

DROP TABLE IF EXISTS `dm_cooperation_bi_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_cooperation_bi_history` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `cooperation_bi_id` bigint(20) unsigned NOT NULL COMMENT '合作方ID',
  `change_item` varchar(255) DEFAULT NULL COMMENT '变更项',
  `source_content` varchar(1000) DEFAULT NULL COMMENT '变更前',
  `target_content` varchar(1000) DEFAULT NULL COMMENT '变更后',
  `create_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` varchar(255) DEFAULT NULL COMMENT '创建人id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=110 DEFAULT CHARSET=utf8 COMMENT='信息变更记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_cooperation_eas`
--

DROP TABLE IF EXISTS `dm_cooperation_eas`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_cooperation_eas` (
  `eas_code` varchar(255) NOT NULL COMMENT 'eas合作方编码',
  `cooperation_type` tinyint(3) unsigned NOT NULL COMMENT '合作方类型。1=上游客户；2=下游供应商',
  `cp_name` varchar(255) DEFAULT NULL COMMENT '客户简称（公司简称）',
  `cp_code` varchar(255) DEFAULT NULL COMMENT '助记码（类似简称/英文缩写）',
  `company_name` varchar(255) NOT NULL COMMENT '公司全称（营业执照全称）',
  `company_taxkey` varchar(500) DEFAULT NULL COMMENT '税务登记号',
  `company_legal` varchar(255) DEFAULT NULL COMMENT '公司法人代表（营业执照）',
  `company_tel` varchar(255) DEFAULT NULL COMMENT '公司电话',
  `company_contact` varchar(255) DEFAULT NULL COMMENT '公司联系人（对接人）',
  `contact_phone` varchar(255) DEFAULT NULL COMMENT '公司联系人手机',
  `contact_mail` varchar(255) DEFAULT NULL COMMENT '联系邮箱',
  `contact_fax` varchar(255) DEFAULT NULL COMMENT '传真',
  `company_website` varchar(255) DEFAULT NULL COMMENT '公司网址（官网）',
  `company_address` varchar(500) DEFAULT NULL COMMENT '公司地址',
  `parent_industry` varchar(255) DEFAULT NULL COMMENT '所属行业（一级）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` varchar(255) DEFAULT NULL COMMENT '创建人（公司员工）',
  `create_department` varchar(255) DEFAULT NULL COMMENT '创建人所属部门',
  `is_approved` tinyint(3) unsigned DEFAULT NULL COMMENT '核准状态（财务审查核实）',
  `is_self` tinyint(3) unsigned DEFAULT NULL COMMENT '是否集团内公司',
  `last_status` tinyint(3) unsigned DEFAULT NULL COMMENT '当前跟进状态',
  `last_remark` varchar(500) DEFAULT NULL COMMENT '最新跟进记录',
  `data_source` tinyint(3) unsigned NOT NULL COMMENT '数据来源标识',
  `is_relate_contract` tinyint(3) unsigned DEFAULT NULL COMMENT '是否关联合同',
  `contract_id_set` text COMMENT '关联合同ID（集合）',
  `update_user` varchar(255) DEFAULT NULL COMMENT '更新人',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '客户信息更新时间',
  `update_version` int(10) unsigned DEFAULT NULL COMMENT '信息更新版本记录号',
  `related_cooperation_id` bigint(20) unsigned DEFAULT NULL COMMENT '关联汇总表（dm_cooperation）的id',
  PRIMARY KEY (`eas_code`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合作伙伴 源表（EAS金蝶）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_cooperation_eas_history`
--

DROP TABLE IF EXISTS `dm_cooperation_eas_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_cooperation_eas_history` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT '主键',
  `eas_code` varchar(255) NOT NULL COMMENT 'eas合作方编码',
  `change_item` varchar(255) DEFAULT NULL COMMENT '变更项',
  `source_content` varchar(1000) DEFAULT NULL COMMENT '变更前',
  `target_content` varchar(1000) DEFAULT NULL COMMENT '变更后',
  `create_user` varchar(255) DEFAULT NULL COMMENT '创建人',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='eas合作伙伴信息变更记录';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_cooperation_status_switch_history`
--

DROP TABLE IF EXISTS `dm_cooperation_status_switch_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_cooperation_status_switch_history` (
  `id` bigint(20) unsigned NOT NULL AUTO_INCREMENT COMMENT 'ID',
  `cooperation_id` bigint(20) unsigned DEFAULT NULL COMMENT '合作方ID',
  `status` tinyint(3) unsigned DEFAULT NULL COMMENT '跟进状态。合作状态：1=启用（默认），2=停用（暂停合作）',
  `remark` varchar(500) DEFAULT NULL COMMENT '跟进记录',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user_id` varchar(255) DEFAULT NULL COMMENT '创建人id（公司员工）',
  `create_user_name` varchar(255) DEFAULT NULL COMMENT '创建人（公司员工）',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=74 DEFAULT CHARSET=utf8 COMMENT='合作伙伴跟进状态';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_cooperation_youtop`
--

DROP TABLE IF EXISTS `dm_cooperation_youtop`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_cooperation_youtop` (
  `id` bigint(20) unsigned NOT NULL COMMENT 'youtop的广告主或者媒体id',
  `cooperation_type` tinyint(3) unsigned NOT NULL COMMENT '合作方类型。1=上游客户；2=下游供应商',
  `cp_name` varchar(255) NOT NULL COMMENT '客户简称（公司简称）',
  `company_name` varchar(255) NOT NULL COMMENT '公司全称（营业执照全称）',
  `company_contact` varchar(255) NOT NULL COMMENT '公司联系人（对接人）',
  `contact_phone` varchar(255) NOT NULL COMMENT '公司联系人手机',
  `company_website` varchar(255) DEFAULT NULL COMMENT '公司网址（官网）',
  `company_address` varchar(500) NOT NULL COMMENT '公司地址',
  `parent_industry` varchar(255) NOT NULL COMMENT '所属行业（一级）',
  `child_industry` varchar(255) NOT NULL COMMENT '所属行业（二级）',
  `create_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '创建时间',
  `create_user` varchar(255) NOT NULL COMMENT '创建人（公司员工）',
  `last_status` tinyint(3) unsigned DEFAULT NULL COMMENT '当前跟进状态',
  `last_remark` varchar(500) DEFAULT NULL COMMENT '最新跟进记录',
  `data_source` tinyint(3) unsigned NOT NULL COMMENT '数据来源标识',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP COMMENT '客户信息更新时间',
  `update_version` int(10) unsigned DEFAULT NULL COMMENT '信息更新版本记录号',
  `related_cooperation_id` bigint(20) unsigned DEFAULT NULL COMMENT '关联汇总表（dm_cooperation）的id',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8 COMMENT='合作伙伴 源表（Youtop）';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Table structure for table `dm_industry`
--

DROP TABLE IF EXISTS `dm_industry`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!40101 SET character_set_client = utf8 */;
CREATE TABLE `dm_industry` (
  `id` int(10) unsigned NOT NULL AUTO_INCREMENT COMMENT '自增ID',
  `parent_industry` varchar(100) DEFAULT NULL COMMENT '行业分类（一级）',
  `child_industry` varchar(100) DEFAULT NULL COMMENT '行业分类（二级）',
  `data_source` varchar(100) DEFAULT NULL COMMENT '数据来源，当前为IT桔子',
  `update_time` datetime NOT NULL DEFAULT CURRENT_TIMESTAMP COMMENT '更新日期',
  `update_user` varchar(255) DEFAULT NULL,
  `is_valid` tinyint(3) unsigned DEFAULT '1' COMMENT '是否有效。0：否；1：是。',
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=213 DEFAULT CHARSET=utf8 COMMENT='BI系统维护的行业分类表';
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping routines for database 'bi_data_management_dev'
--

--
-- Temporary table structure for view `dm_cooperation_source`
--

CREATE OR REPLACE
ALGORITHM = UNDEFINED VIEW `dm_cooperation_source` AS
select
    `dm_cooperation_eas`.`eas_code` AS `id`,
    `dm_cooperation_eas`.`cooperation_type` AS `cooperation_type`,
    `dm_cooperation_eas`.`cp_name` AS `cp_name`,
    `dm_cooperation_eas`.`company_name` AS `company_name`,
    `dm_cooperation_eas`.`company_taxkey` AS `company_taxkey`,
    `dm_cooperation_eas`.`company_contact` AS `company_contact`,
    `dm_cooperation_eas`.`contact_phone` AS `contact_phone`,
    `dm_cooperation_eas`.`company_website` AS `company_website`,
    `dm_cooperation_eas`.`company_address` AS `company_address`,
    `dm_cooperation_eas`.`create_user` AS `create_user`,
    `dm_cooperation_eas`.`create_department` AS `create_department`,
    `dm_cooperation_eas`.`data_source` AS `data_source`,
    `dm_cooperation_eas`.`create_time` AS `create_time`,
    `dm_cooperation_eas`.`update_time` AS `update_time`,
    `dm_cooperation_eas`.`related_cooperation_id` AS `related_cooperation_id`,
    0 AS `is_protection`,
    NULL AS `create_user_id`,
    NULL AS `create_department_code`
from
    `dm_cooperation_eas`
union all
select
    `dm_cooperation_bi`.`id` AS `id`,
    `dm_cooperation_bi`.`cooperation_type` AS `cooperation_type`,
    `dm_cooperation_bi`.`cp_name` AS `cp_name`,
    `dm_cooperation_bi`.`company_name` AS `company_name`,
    `dm_cooperation_bi`.`company_taxkey` AS `company_taxkey`,
    `dm_cooperation_bi`.`company_contact` AS `company_contact`,
    `dm_cooperation_bi`.`contact_phone` AS `contact_phone`,
    `dm_cooperation_bi`.`company_website` AS `company_website`,
    `dm_cooperation_bi`.`company_address` AS `company_address`,
    `dm_cooperation_bi`.`create_user` AS `create_user`,
    `dm_cooperation_bi`.`create_department` AS `create_department`,
    `dm_cooperation_bi`.`data_source` AS `data_source`,
    `dm_cooperation_bi`.`create_time` AS `create_time`,
    `dm_cooperation_bi`.`update_time` AS `update_time`,
    `dm_cooperation_bi`.`related_cooperation_id` AS `related_cooperation_id`,
    `dm_cooperation_bi`.`is_protection` AS `is_protection`,
    `dm_cooperation_bi`.`create_user_id` AS `create_user_id`,
    `dm_cooperation_bi`.`create_department_code` AS `create_department_code`
from
    `dm_cooperation_bi`
union all
select
    `dm_cooperation_youtop`.`id` AS `id`,
    `dm_cooperation_youtop`.`cooperation_type` AS `cooperation_type`,
    `dm_cooperation_youtop`.`cp_name` AS `cp_name`,
    `dm_cooperation_youtop`.`company_name` AS `company_name`,
    NULL AS `company_taxkey`,
    `dm_cooperation_youtop`.`company_contact` AS `company_contact`,
    `dm_cooperation_youtop`.`contact_phone` AS `contact_phone`,
    `dm_cooperation_youtop`.`company_website` AS `company_website`,
    `dm_cooperation_youtop`.`company_address` AS `company_address`,
    `dm_cooperation_youtop`.`create_user` AS `create_user`,
    NULL AS `create_department`,
    `dm_cooperation_youtop`.`data_source` AS `data_source`,
    `dm_cooperation_youtop`.`create_time` AS `create_time`,
    `dm_cooperation_youtop`.`update_time` AS `update_time`,
    `dm_cooperation_youtop`.`related_cooperation_id` AS `related_cooperation_id`,
    0 AS `is_protection`,
    NULL AS `create_user_id`,
    NULL AS `create_department_code`
from
    `dm_cooperation_youtop`;

-- Dump completed on 2020-07-30 16:36:28
