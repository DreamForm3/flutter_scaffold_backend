package com.lakecloud.scaffold.service.impl;

import com.lakecloud.scaffold.entity.UserMsg;
import com.lakecloud.scaffold.mapper.UserMsgMapper;
import com.lakecloud.scaffold.service.UserMsgService;
import com.lakecloud.scaffold.param.UserMsgPageParam;
import com.lakecloud.scaffold.vo.UserMsgQueryVo;
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
 * 用户消息 服务实现类
 *
 * @author Max.King
 * @since 2022-02-22
 */
@Slf4j
@Service
public class UserMsgServiceImpl extends BaseServiceImpl<UserMsgMapper, UserMsg> implements UserMsgService {

    @Autowired
    private UserMsgMapper userMsgMapper;

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean saveUserMsg(UserMsg userMsg) throws Exception {
        return super.save(userMsg);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean updateUserMsg(UserMsg userMsg) throws Exception {
        userMsg.setUpdateTime(LocalDateTime.now());
        return super.updateById(userMsg);
    }

    @Transactional(rollbackFor = Exception.class)
    @Override
    public boolean deleteUserMsg(Long id) throws Exception {
        return super.removeById(id);
    }

    @Override
    public UserMsgQueryVo getUserMsgById(Serializable id) {
        return userMsgMapper.getUserMsgById(id);
    }

    @Override
    public Paging<UserMsgQueryVo> getUserMsgPageList(UserMsgPageParam userMsgPageParam) throws Exception {
        // 默认按照ID倒序排序
        Page<UserMsgQueryVo> page = new PageInfo<>(userMsgPageParam, OrderItem.desc("id"));
        IPage<UserMsgQueryVo> iPage = userMsgMapper.getUserMsgPageList(page, userMsgPageParam);
        return new Paging<UserMsgQueryVo>(iPage);
    }

}
