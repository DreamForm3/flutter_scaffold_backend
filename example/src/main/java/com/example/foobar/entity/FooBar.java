package com.example.foobar.entity;

import io.geekidea.springbootplus.framework.common.entity.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import java.util.Date;
import com.baomidou.mybatisplus.annotation.Version;
import com.baomidou.mybatisplus.annotation.TableId;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import io.geekidea.springbootplus.framework.core.validator.groups.Update;

/**
 * FooBar
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "FooBar对象")
public class FooBar extends BaseEntity {
    private static final long serialVersionUID = 1L;

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
}
