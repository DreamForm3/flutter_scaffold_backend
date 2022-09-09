package com.lakecloud.scaffold.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.lakecloud.scaffold.entity.UserMsg;
import com.lakecloud.scaffold.param.UserMsgPageParam;
import com.lakecloud.scaffold.vo.UserMsgQueryVo;

import org.springframework.stereotype.Repository;

import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import org.apache.ibatis.annotations.Param;
import java.io.Serializable;

/**
 * 用户消息 Mapper 接口
 *
 * @author Max.King
 * @since 2022-02-22
 */
@Repository
public interface UserMsgMapper extends BaseMapper<UserMsg> {

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     */
    UserMsgQueryVo getUserMsgById(Serializable id);

    /**
     * 获取分页对象
     *
     * @param page
     * @param userMsgPageParam
     * @return
     */
    IPage<UserMsgQueryVo> getUserMsgPageList(@Param("page") Page page, @Param("param") UserMsgPageParam userMsgPageParam);

}
