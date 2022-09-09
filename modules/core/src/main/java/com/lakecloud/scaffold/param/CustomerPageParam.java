package com.lakecloud.scaffold.param;

import io.geekidea.springbootplus.framework.core.pagination.BasePageOrderParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.Accessors;

/**
 * <pre>
 * 客户 分页参数对象
 * </pre>
 *
 * @author Alex.King
 * @date 2020-09-17
 */
@Data
@Accessors(chain = true)
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "客户分页参数")
public class CustomerPageParam extends BasePageOrderParam {
    private static final long serialVersionUID = 1L;

    @ApiModelProperty("客户姓名")
    private String name;
    @ApiModelProperty("联系方式")
    private String contactInfo;
}
