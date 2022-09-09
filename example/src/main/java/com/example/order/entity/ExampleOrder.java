package com.example.order.entity;

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
 * 订单示例
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "ExampleOrder对象")
public class ExampleOrder extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("订单名称")
    private String name;
    @ApiModelProperty("订单编号")
    private String orderNo;
    @ApiModelProperty("备注")
    private String remark;
    @ApiModelProperty("状态，0：禁用，1：启用")
    private Integer state;
}
