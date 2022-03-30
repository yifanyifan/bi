package com.stnts.bi.entity.sys;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.stnts.bi.groups.UpdateGroup;
import io.swagger.annotations.ApiImplicitParams;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.experimental.Accessors;

import javax.validation.constraints.NotNull;
import java.util.Collections;
import java.util.Date;
import java.util.List;

/**
 * @author: liang.zhang
 * @description:
 * @date: 2021/5/28
 */
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = false)
@Getter
@Setter
@ApiModel("数据层级类")
@TableName("stbi_sdk_data_level")
public class SdkDataLevelEntity {

    @NotNull(groups = UpdateGroup.class, message = "数据层级ID不允许为空")
    @ApiModelProperty("数据层级ID")
    @TableId(value = "level_id", type = IdType.AUTO)
    private Integer levelId;
    @ApiModelProperty("数据层级名称")
    private String levelName;
    @ApiModelProperty("父层级ID, 默认值为-1表示第一层")
    private Integer pid ;
    @ApiModelProperty("层级路径(path=父path.父id), 以.分割的ID字符串, 第一层级path为空字符串, ID为1的子节点path为.1")
    private String path ;
    @ApiModelProperty("层级类型, 1: 普通目录; 2: 叶子目录")
    private Integer type;
    @ApiModelProperty("索引")
    private Integer idx;
    @ApiModelProperty("创建者ID")
    @TableField("created_by")
    private Integer createdBy;

    @ApiModelProperty("当前产品线是否绑定了此数据层级")
    @TableField(exist = false)
    private boolean checked;

    @ApiModelProperty("子节点, 在树结构中用到")
    @TableField(exist = false)
    private List<SdkDataLevelEntity> children = Collections.emptyList();

    @ApiModelProperty(value="创建时间", example="2020-03-26 05:54:57")
    @TableField("created_at")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    protected Date createdAt = new Date();
    @ApiModelProperty(value="修改时间", example="2020-03-26 05:54:57")
    @TableField("updated_at")
    @JsonFormat(pattern="yyyy-MM-dd HH:mm:ss", timezone="GMT+8")
    protected Date updatedAt = new Date();
}
