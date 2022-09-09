package com.lakecloud.scaffold.service;

import com.lakecloud.scaffold.entity.UserMsg;
import com.lakecloud.scaffold.param.UserMsgPageParam;
import io.geekidea.springbootplus.framework.common.service.BaseService;
import com.lakecloud.scaffold.vo.UserMsgQueryVo;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import java.io.Serializable;

/**
 * 用户消息 服务类
 *
 * @author Max.King
 * @since 2022-02-22
 */
public interface UserMsgService extends BaseService<UserMsg> {

    /**
     * 保存
     *
     * @param userMsg
     * @return
     * @throws Exception
     */
    boolean saveUserMsg(UserMsg userMsg) throws Exception;

    /**
     * 修改
     *
     * @param userMsg
     * @return
     * @throws Exception
     */
    boolean updateUserMsg(UserMsg userMsg) throws Exception;

    /**
     * 删除
     *
     * @param id
     * @return
     * @throws Exception
     */
    boolean deleteUserMsg(Long id) throws Exception;

    /**
     * 根据ID获取查询对象
     *
     * @param id
     * @return
     * @throws Exception
     */
    UserMsgQueryVo getUserMsgById(Serializable id);

    /**
     * 获取分页对象
     *
     * @param userMsgPageParam
     * @return
     * @throws Exception
     */
    Paging<UserMsgQueryVo> getUserMsgPageList(UserMsgPageParam userMsgPageParam) throws Exception;

}
