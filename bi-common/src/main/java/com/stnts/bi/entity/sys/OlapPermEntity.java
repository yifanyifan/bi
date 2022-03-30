package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;

import java.util.Date;
import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/1/6
 */
@Getter
@Setter
@TableName("stbi_perm_olap")
@ApiModel("OLAP打通权限类")
public class OlapPermEntity implements Comparable{

    @ApiModelProperty("权限ID")
    private String permId;
    @ApiModelProperty("权限名称")
    private String permName;
    @ApiModelProperty("别名")
    private String permNickname;
    @ApiModelProperty("父权限ID")
    private String parentPermId;
    @ApiModelProperty("权限CODE")
    private String permCode;
    @ApiModelProperty("权限类型：1.目录 2.页面")
    private Integer permType;
    @ApiModelProperty("权限排序")
    private Integer orderNum;
    @ApiModelProperty("权限备注")
    private String permDesc;
    @ApiModelProperty("是否启用 1启用 0停用")
    private Integer status;
    @ApiModelProperty("对应BI中的权限ID")
    private Integer biPermId;
    @ApiModelProperty("独眼OLAP中的权限ID")
    private Integer olapPermId;
    @ApiModelProperty("对应BI中的产品线ID")
    private Integer productId;
    @TableField(exist=false)
    @ApiModelProperty("根节点ID")
    private Integer rootId;

    @TableField(exist = false)
    @ApiModelProperty("子节点")
    private List<OlapPermEntity> children;

    @TableField(exist = false)
    @ApiModelProperty("请求的URL")
    private String url;

    @ApiModelProperty("有权限的用户")
    @TableField(exist = false)
    private List<UserEntity> users;

    @ApiModelProperty(value="创建时间", example="2020-03-26 05:54:57")
    @TableField("created_at")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    protected Date createdAt = new Date();
    @ApiModelProperty(value="修改时间", example="2020-03-26 05:54:57")
    @TableField("updated_at")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    protected Date updatedAt = new Date();

    public String getPermName(){
        return StringUtils.isNotEmpty(permNickname) ? permNickname : permName;
    }

    @Override
    public int compareTo(Object o) {

        if(o instanceof OlapPermEntity){
            OlapPermEntity other = (OlapPermEntity) o;
            if(this.getOrderNum() < other.getOrderNum()){
                return -1;
            }else if(this.getOrderNum() > other.getOrderNum()){
                return 1;
            }
        }
        return 0;
    }
}
