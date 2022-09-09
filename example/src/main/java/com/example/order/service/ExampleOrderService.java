package com.example.order.service;

import com.example.order.entity.ExampleOrder;
import com.example.order.param.ExampleOrderPageParam;
import io.geekidea.springbootplus.framework.common.service.BaseService;
import com.example.order.vo.ExampleOrderQueryVo;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import java.io.Serializable;

/**
 * 订单示例 服务类
 *
 * @author Alex.King
 * @since 2020-09-17
 */
public interface ExampleOrderService extends BaseService<ExampleOrder> {

    /**
     * 保存
     *
     * @param exampleOrder
     * @return
     * @throws Exception
     */
    boolean saveExampleOrder(ExampleOrder exampleOrder) throws Exception;

    /**
     * 修改
     *
     * @param exampleOrder
     * @return
     * @throws Exception
     */
    boolean updateExampleOrder(ExampleOrder exampleOrder) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteExampleOrder(Long id) throws Exception;

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     * @throws Exception
     */
    ExampleOrderQueryVo getExampleOrderById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     *
     * @param exampleOrderQueryParam
     * @return
     * @throws Exception
     */
    Paging<ExampleOrderQueryVo> getExampleOrderPageList(ExampleOrderPageParam exampleOrderPageParam) throws Exception;

}
