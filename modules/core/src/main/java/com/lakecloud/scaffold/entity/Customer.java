package com.lakecloud.scaffold.entity;

import io.geekidea.springbootplus.framework.common.entity.BaseEntity;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

/**
 * 客户
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "Customer对象")
public class Customer extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("客户姓名")
    private String name;
    @ApiModelProperty("联系方式")
    private String contactInfo;
    @ApiModelProperty("创建用户")
    private Long createUser;
    @ApiModelProperty("状态，0：禁用，1：启用")
    private Integer state;
    @ApiModelProperty("备注")
    private String remark;
}
