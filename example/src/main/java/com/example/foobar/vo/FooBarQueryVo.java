package com.example.foobar.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * FooBar 查询结果对象
 * </pre>
 *
 * @author Alex.King
 * @date 2020-09-17
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "FooBarQueryVo对象")
public class FooBarQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("Name")
    private String name;

    @ApiModelProperty("Foo")
    private String foo;

    @ApiModelProperty("Bar")
    private String bar;

    @ApiModelProperty("Remark")
    private String remark;

    @ApiModelProperty("State，0：Disable，1：Enable")
    private Integer state;

    @ApiModelProperty("Version")
    private Integer version;

    @ApiModelProperty("Create Time")
    private Date createTime;

    @ApiModelProperty("Update Time")
    private Date updateTime;
}