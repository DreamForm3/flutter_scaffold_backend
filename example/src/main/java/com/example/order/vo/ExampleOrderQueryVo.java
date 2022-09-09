package com.example.order.vo;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;
import java.io.Serializable;
import java.util.Date;

/**
 * <pre>
 * 订单示例 查询结果对象
 * </pre>
 *
 * @author Alex.King
 * @date 2020-09-17
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "ExampleOrderQueryVo对象")
public class ExampleOrderQueryVo implements Serializable {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("主键")
    private Long id;

    @ApiModelProperty("订单名称")
    private String name;

    @ApiModelProperty("订单编号")
    private String orderNo;

    @ApiModelProperty("备注")
    private String remark;

    @ApiModelProperty("状态，0：禁用，1：启用")
    private Integer state;

    @ApiModelProperty("版本")
    private Integer version;

    @ApiModelProperty("创建时间")
    private Date createTime;

    @ApiModelProperty("修改时间")
    private Date updateTime;
}