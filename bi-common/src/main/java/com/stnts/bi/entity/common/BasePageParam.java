/*
 * Copyright 2019-2029
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.stnts.bi.entity.common;

import com.baomidou.mybatisplus.core.metadata.OrderItem;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 查询参数
 *
 * @author stnts
 * @since 2018-11-08
 */
@Data
@ApiModel("查询参数对象")
public abstract class BasePageParam implements Serializable {
    private static final long serialVersionUID = -3263921252635611410L;

    @ApiModelProperty(value = "页码,默认为1", example = "1")
    private Long pageIndex = 1L;

    @ApiModelProperty(value = "页大小,默认为10", example = "10")
    private Long pageSize = 10L;

    @ApiModelProperty(value = "搜索字符串", example = "")
    private String keyword;

    @ApiModelProperty(value = "排序集合", example = "")
    private List<OrderItem> orders;

    public void setPageIndex(Long pageIndex) {
        if (pageIndex == null || pageIndex <= 0) {
            this.pageIndex = 1L;
        } else {
            this.pageIndex = pageIndex;
        }
    }

    public void setPageSize(Long pageSize) {
        if (pageSize == null || pageSize <= 0) {
            this.pageSize = 10L;
        } else {
            this.pageSize = pageSize;
        }
    }

}
