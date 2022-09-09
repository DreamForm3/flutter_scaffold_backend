package com.example.order.param;

import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;
import io.geekidea.springbootplus.framework.core.pagination.BasePageOrderParam;

/**
 * <pre>
 * 订单示例 分页参数对象
 * </pre>
 *
 * @author Alex.King
 * @date 2020-09-17
 */
@Data
@Accessors(chain = true)
@EqualsAndHashCode(callSuper = true)
@ApiModel(value = "订单示例分页参数")
public class ExampleOrderPageParam extends BasePageOrderParam {
    private static final long serialVersionUID = 1L;
}
