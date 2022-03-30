package com.stnts.bi.entity.sys;

import java.io.Serializable;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import cn.hutool.core.collection.CollectionUtil;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stnts.bi.vo.OlapPermSubVO;
import com.stnts.bi.vo.SimplePermVO;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import io.swagger.annotations.ApiOperation;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

/**
 * @author liang.zhang
 * @date 2020年3月29日
 * @desc TODO
 */
@Data
@TableName("stbi_user")
@JsonIgnoreProperties(value = {"handler"})
@ApiModel("用户类")
public class UserEntity implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = -6709450893921657519L;
	
	@ApiModelProperty(value="用户ID")
	@TableId(value="user_id", type=IdType.AUTO)
	private Integer id;
	@ApiModelProperty(value="用户名")
	private String cnname;
	@JsonIgnore
	@ApiModelProperty(value="OA状态")
	private Integer oaStatus;
	@JsonIgnore
	@ApiModelProperty(value="手机号")
	private String mobile;
	@JsonIgnore
	@ApiModelProperty(value="工号")
	private String cardNumber;
	@ApiModelProperty(value="邮箱")
	private String email;
	@JsonIgnore
	@ApiModelProperty(value="平台ID")
	private String personId;
	@ApiModelProperty(value="部门CODE码")
	private String code;
	@ApiModelProperty("是否超级管理员")
	@TableField(value="is_admin")
	private Integer admin = 0;
	@ApiModelProperty(value="创建时间", example="2020-03-26 13:54:57")
	@JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
	private Date createdAt = new Date();
	
	@TableField(exist=false)
	@ApiModelProperty(value="一级部门名称")
	private String departmentName;

	@TableField(exist=false)
	@ApiModelProperty(value="完整部门CODE, 目前用不到")
	private String departmentCode;
	
	@TableField(exist=false)
	private List<UserRoleEntity> roles;

	@TableField(exist = false)
	private List<OrgEntity> orgs;

	@TableField(exist = false)
	private List<ProductEntity> products;
	
	@ApiModelProperty("权限树[前端控制权限用]")
	@TableField(exist=false)
	private List<SimplePermVO> permTree;
	@ApiModelProperty("权限code set[后端做权限验证用]")
	@TableField(exist=false)
	private Set<String> permSet;
	@ApiModelProperty("权限产品线code set[后端做权限验证用]")
	@TableField(exist = false)
	private Set<String> productSet;
	@ApiModelProperty("SDK权限code map[后端做权限验证用]")
	@TableField(exist = false)
	private Map<String, List<String>> sdkPermMap;

	/**
	 * olap
	 */
	@ApiModelProperty("OLAP对应的根目录权限")
	@TableField(exist = false)
	private List<OlapPermSubVO> olapPermList;
    /**
     * 保留set命名  结构改为map
     */
	@ApiModelProperty("BI-OLAP根目录权限SET")
	@TableField(exist = false)
	private Map<String, List<String>> olapPermSet;
	@TableField(exist = false)
	@ApiModelProperty("BI-OLAP用户对应的权限树")
	private List<OlapPermEntity> olapPermTree;
	@TableField(exist = false)
	@ApiModelProperty("BI-OLAP权限映射表")
	private List<OlapPermSubVO> olapPermDict;

	@ApiModelProperty("部门组织表格显示")
	@TableField(exist = false)
	private String orgNames;
	@ApiModelProperty("用户角色表格显示")
	@TableField(exist = false)
	private String roleNames;

	public String getCnname(){
		try{
			if(cnname.contains("(")){
				return cnname;
			}
			return this.cnname.concat("(").concat(this.cardNumber).concat(")");
		}catch(Exception e){
			return this.cnname;
		}
	}

	public String getOrgNames(){
		if(CollectionUtil.isNotEmpty(orgs)){
			return orgs.stream().map(OrgEntity::getOrgName).collect(Collectors.joining(", "));
		}
		return null;
	}

	public String getProductNames(){
		if(CollectionUtil.isNotEmpty(products)){
			return products.stream().map(ProductEntity::getProductName).collect(Collectors.joining(", "));
		}
		return null;
	}

	public String getRoleNames(){
		if(CollectionUtil.isNotEmpty(roles)){
			return roles.stream().map(UserRoleEntity::getRoleName).collect(Collectors.joining(", "));
		}
		return null;
	}
}
