package com.lakecloud.scaffold.service;

import com.lakecloud.scaffold.entity.Customer;
import com.lakecloud.scaffold.param.CustomerPageParam;
import com.lakecloud.scaffold.vo.CustomerQueryVo;
import io.geekidea.springbootplus.framework.common.service.BaseService;
import io.geekidea.springbootplus.framework.core.pagination.Paging;

import java.io.Serializable;
import java.util.List;

/**
 * 客户 服务类
 *
 * @author Alex.King
 * @since 2020-09-17
 */
public interface CustomerService extends BaseService<Customer> {

    /**
     * 保存
     *
     * @param customer
     * @return
     * @throws Exception
     */
    boolean saveCustomer(Customer customer) throws Exception;

    /**
     * 修改
     *
     * @param customer
     * @return
     * @throws Exception
     */
    boolean updateCustomer(Customer customer) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteCustomer(Long id) throws Exception;

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     * @throws Exception
     */
    CustomerQueryVo getCustomerById(Serializable id);

    /**
     * 获取分页对象
     *
     * @param customerPageParam
     * @return
     * @throws Exception
     */
    Paging<CustomerQueryVo> getCustomerPageList(CustomerPageParam customerPageParam) throws Exception;

    /**
     * 获取所有的顾客列表
     * @return
     */
    List<CustomerQueryVo> getAllCustomerList();
}
