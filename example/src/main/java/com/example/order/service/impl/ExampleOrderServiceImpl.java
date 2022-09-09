package com.example.order.service.impl;

import com.example.order.entity.ExampleOrder;
import com.example.order.mapper.ExampleOrderMapper;
import com.example.order.service.ExampleOrderService;
import com.example.order.param.ExampleOrderPageParam;
import com.example.order.vo.ExampleOrderQueryVo;
import io.geekidea.springbootplus.framework.common.service.impl.BaseServiceImpl;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import io.geekidea.springbootplus.framework.core.pagination.PageInfo;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.springframework.transaction.annotation.Transactional;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.beans.factory.annotation.Autowired;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 订单示例 服务实现类
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Slf4j
@Service
public class ExampleOrderServiceImpl extends BaseServiceImpl<ExampleOrderMapper, ExampleOrder> implements ExampleOrderService {

    @Autowired
    private ExampleOrderMapper exampleOrderMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveExampleOrder(ExampleOrder exampleOrder) throws Exception {
        return super.save(exampleOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateExampleOrder(ExampleOrder exampleOrder) throws Exception {
        exampleOrder.setUpdateTime(LocalDateTime.now());
        return super.updateById(exampleOrder);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteExampleOrder(Long id) throws Exception {
        return super.removeById(id);
    }

    @Override
    public ExampleOrderQueryVo getExampleOrderById(Serializable id) throws Exception {
    return exampleOrderMapper.getExampleOrderById(id);
    }

    @Override
    public Paging<ExampleOrderQueryVo> getExampleOrderPageList(ExampleOrderPageParam exampleOrderPageParam) throws Exception {
        // 默认按照ID倒序排序
        Page<ExampleOrderQueryVo> page = new PageInfo<>(exampleOrderPageParam, OrderItem.desc("id"));
        IPage<ExampleOrderQueryVo> iPage = exampleOrderMapper.getExampleOrderPageList(page, exampleOrderPageParam);
        return new Paging<ExampleOrderQueryVo>(iPage);
    }

}
