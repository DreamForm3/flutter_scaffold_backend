package com.example.foobar.service;

import com.example.foobar.entity.FooBar;
import com.example.foobar.param.FooBarPageParam;
import io.geekidea.springbootplus.framework.common.service.BaseService;
import com.example.foobar.vo.FooBarQueryVo;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import java.io.Serializable;

/**
 * FooBar 服务类
 *
 * @author Alex.King
 * @since 2020-09-17
 */
public interface FooBarService extends BaseService<FooBar> {

    /**
     * 保存
     *
     * @param fooBar
     * @return
     * @throws Exception
     */
    boolean saveFooBar(FooBar fooBar) throws Exception;

    /**
     * 修改
     *
     * @param fooBar
     * @return
     * @throws Exception
     */
    boolean updateFooBar(FooBar fooBar) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteFooBar(Long id) throws Exception;

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     * @throws Exception
     */
    FooBarQueryVo getFooBarById(Serializable id) throws Exception;

    /**
     * 获取分页对象
     *
     * @param fooBarQueryParam
     * @return
     * @throws Exception
     */
    Paging<FooBarQueryVo> getFooBarPageList(FooBarPageParam fooBarPageParam) throws Exception;

}
