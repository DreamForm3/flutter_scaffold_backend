package com.lakecloud.scaffold.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lakecloud.scaffold.entity.Customer;
import com.lakecloud.scaffold.param.CustomerPageParam;
import com.lakecloud.scaffold.vo.CustomerQueryVo;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.io.Serializable;

/**
 * 客户 Mapper 接口
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Repository
public interface CustomerMapper extends BaseMapper<Customer> {

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     */
    CustomerQueryVo getCustomerById(Serializable id);

    /**
     * 获取分页对象
     *
     * @param page
     * @param customerPageParam
     * @return
     */
    IPage<CustomerQueryVo> getCustomerPageList(@Param("page") Page page, @Param("param") CustomerPageParam customerPageParam);

}
