package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;

/**
 * @author liang.zhang
 * @date 2020年4月5日
 * @desc TODO
 */
@Getter
@Setter
@ApiModel("权限类")
@TableName("stbi_perm")
public class PermEntity extends BaseEntity{

	/**
	 * 
	 */
	private static final long serialVersionUID = 6784396008723521067L;
	
	@ApiModelProperty("权限ID")
	@TableId(value="perm_id", type=IdType.AUTO)
	private Integer permId;
	@ApiModelProperty("权限名称")
	private String permName;
	@ApiModelProperty("权限描述")
	private String permDesc;
	@ApiModelProperty("父权限ID")
	private Integer parentPermId;
	@ApiModelProperty("权限CODE")
	private String permCode;
	@ApiModelProperty("权限类型")
	private Integer permType;
	@ApiModelProperty("排序")
	private Integer orderNum;
	
	//是否拥有这个权限
	@TableField(exist=false)
	private boolean checked;

}
