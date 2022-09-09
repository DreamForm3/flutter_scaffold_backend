package com.lakecloud.scaffold.service.impl;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.core.metadata.OrderItem;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.lakecloud.scaffold.entity.Customer;
import com.lakecloud.scaffold.mapper.CustomerMapper;
import com.lakecloud.scaffold.param.CustomerPageParam;
import com.lakecloud.scaffold.service.CustomerService;
import com.lakecloud.scaffold.vo.CustomerQueryVo;
import io.geekidea.springbootplus.framework.common.service.impl.BaseServiceImpl;
import io.geekidea.springbootplus.framework.core.pagination.PageInfo;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import io.geekidea.springbootplus.framework.util.RedisUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 客户 服务实现类
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Slf4j
@Service
public class CustomerServiceImpl extends BaseServiceImpl<CustomerMapper, Customer> implements CustomerService {

    @Autowired
    private CustomerMapper customerMapper;
    @Autowired
    private RedisUtils redisUtils;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveCustomer(Customer customer) throws Exception {
        boolean flag = super.save(customer);
        // 新增redis模糊查询的缓存
        redisUtils.updateRedisFuzzySearchValueById(customer.getId(), CustomerQueryVo.class, this::getCustomerById);
        // 新增数据以后，分页查询的缓存要清掉
        redisUtils.clearQueryCache(getClass(), "getCustomerPageList");
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateCustomer(Customer customer) throws Exception {
        customer.setUpdateTime(LocalDateTime.now());
        boolean flag = super.updateById(customer);
        // 更新redis模糊查询的缓存
        redisUtils.updateRedisFuzzySearchValueById(customer.getId(), CustomerQueryVo.class, this::getCustomerById);
        return flag;
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteCustomer(Long id) throws Exception {
        boolean flag = super.removeById(id);
        // 删除数据以后，分页查询的缓存要清掉
        redisUtils.clearQueryCache(getClass(), "getCustomerPageList");
        return flag;
    }

    @Override
    public CustomerQueryVo getCustomerById(Serializable id){
        return customerMapper.getCustomerById(id);
    }

    @Override
    @Cacheable(cacheNames = "5-minutes-cache")
    public Paging<CustomerQueryVo> getCustomerPageList(CustomerPageParam customerPageParam) throws Exception {
        // 默认按照ID倒序排序
        Page<CustomerQueryVo> page = new PageInfo<>(customerPageParam, OrderItem.desc("id"));

        Paging<CustomerQueryVo> paging;
        // 如果没有任何搜索条件，交给数据库做排序和分页是最高效的
        if (StringUtils.isEmpty(customerPageParam.getName())
                && StringUtils.isEmpty(customerPageParam.getContactInfo())) {
            IPage<CustomerQueryVo> iPage = customerMapper.getCustomerPageList(page, customerPageParam);
            paging = new Paging(iPage);
        } else {
            // 由于客户姓名、联系方式都是模糊搜索，数据库like无法走索引，全表扫描会很低效，此时改用redis查询
            String pattern = RedisUtils.getRedisScanPattern(CustomerQueryVo.class, customerPageParam.getName(), customerPageParam.getContactInfo());
            paging = redisUtils.findValuesForPage(pattern, page.getCurrent(), page.getSize(), CustomerQueryVo.class, true);
            // 如果redis搜索不到，再从数据库里查询
            if (paging.getTotal() == 0) {
                IPage<CustomerQueryVo> iPage = customerMapper.getCustomerPageList(page, customerPageParam);
                paging = new Paging(iPage);
            }
        }
        return paging;
    }

    @Override
    public List<CustomerQueryVo> getAllCustomerList() {
        // 默认按照ID倒序排序
        CustomerPageParam customerPageParam = new CustomerPageParam();
        Page<CustomerQueryVo> page = new PageInfo<>(customerPageParam, OrderItem.desc("id"));
        page.setCurrent(1).setSize(Long.MAX_VALUE);
        IPage<CustomerQueryVo> iPage = customerMapper.getCustomerPageList(page, customerPageParam);
        return iPage.getRecords();
    }
}
