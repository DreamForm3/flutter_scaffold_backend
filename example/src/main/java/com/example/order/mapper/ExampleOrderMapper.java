package com.example.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.example.order.entity.ExampleOrder;
import com.example.order.param.ExampleOrderPageParam;
import com.example.order.vo.ExampleOrderQueryVo;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import java.io.Serializable;

/**
 * 订单示例 Mapper 接口
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Repository
public interface ExampleOrderMapper extends BaseMapper<ExampleOrder> {

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     */
    ExampleOrderQueryVo getExampleOrderById(Serializable id);

    /**
     * 获取分页对象
     *
     * @param page
     * @param exampleOrderQueryParam
     * @return
     */
    IPage<ExampleOrderQueryVo> getExampleOrderPageList(@Param("page") Page page, @Param("param") ExampleOrderPageParam exampleOrderPageParam);

}
