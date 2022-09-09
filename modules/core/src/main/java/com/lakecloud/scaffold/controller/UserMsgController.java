package com.lakecloud.scaffold.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.lakecloud.scaffold.entity.UserMsg;
import com.lakecloud.scaffold.enums.UserMsgStatusEnum;
import com.lakecloud.scaffold.service.UserMsgService;
import io.geekidea.springbootplus.framework.util.LoginUtil;
import io.geekidea.springbootplus.system.entity.SysUser;
import io.geekidea.springbootplus.system.service.SysUserService;
import lombok.extern.slf4j.Slf4j;
import com.lakecloud.scaffold.param.UserMsgPageParam;
import io.geekidea.springbootplus.framework.common.controller.BaseController;
import com.lakecloud.scaffold.vo.UserMsgQueryVo;
import io.geekidea.springbootplus.framework.common.api.ApiResult;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import io.geekidea.springbootplus.framework.common.param.IdParam;
import io.geekidea.springbootplus.framework.common.api.ApiCode;
import io.geekidea.springbootplus.framework.log.annotation.Module;
import io.geekidea.springbootplus.framework.log.annotation.OperationLog;
import io.geekidea.springbootplus.framework.log.enums.OperationLogType;
import io.geekidea.springbootplus.framework.core.validator.groups.Add;
import io.geekidea.springbootplus.framework.core.validator.groups.Update;
import org.apache.commons.lang3.StringUtils;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;


/**
 * 用户消息 控制器
 *
 * @author Max.King
 * @since 2022-02-22
 */
@Slf4j
@RestController
@RequestMapping("/userMsgs")
@Module("scaffold")
@Api(value = "用户消息API", tags = {"用户消息"})
public class UserMsgController extends BaseController {

    @Autowired
    private UserMsgService userMsgService;
    @Autowired
    private SysUserService sysUserService;

    /**
     * 添加用户消息
     */
    @PostMapping("")
    @OperationLog(name = "添加用户消息", type = OperationLogType.ADD)
    @ApiOperation(value = "添加用户消息", response = ApiResult.class)
    public ApiResult<Boolean> addUserMsg(@Validated(Add.class) @RequestBody UserMsg userMsg, HttpServletResponse response) {
        boolean flag = false;

        if (StringUtils.isEmpty(userMsg.getTitle())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "消息标题不能为空", false);
        }
        // 接收用户不存在
        if (userMsg.getReceiveUser() == null
                || sysUserService.count(new QueryWrapper<SysUser>((SysUser) new SysUser()
                .setId(userMsg.getReceiveUser())
                .setIsDelete(false))) <= 0) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "接收用户不存在", false);
        }

        // 设置默认值
        userMsg.setSendUser(LoginUtil.getUserId());
        userMsg.setStatus(UserMsgStatusEnum.UNREAD);
        userMsg.setSentTime(new Date());

        try {
            flag = userMsgService.saveUserMsg(userMsg);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("添加用户消息发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    // 不允许修改消息
//    /**
//     * 修改用户消息
//     */
//    @PutMapping("/{id}")
//    @OperationLog(name = "修改用户消息", type = OperationLogType.UPDATE)
//    @ApiOperation(value = "修改用户消息", response = ApiResult.class)
//    public ApiResult<Boolean> updateUserMsg(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody UserMsg userMsg, HttpServletResponse response) {
//        boolean flag = false;
//        try {
//            userMsg.setId(id);
//            flag = userMsgService.updateUserMsg(userMsg);
//            response.setStatus(HttpServletResponse.SC_OK);
//        } catch (Exception e) {
//            log.error("修改用户消息发生异常", e);
//            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
//        }
//        return ApiResult.result(flag);
//    }

    /**
     * 删除用户消息
     */
    @DeleteMapping("/{id}")
    @OperationLog(name = "删除用户消息", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除用户消息", response = ApiResult.class)
    public ApiResult<Boolean> deleteUserMsg(@PathVariable("id") Long id, HttpServletResponse response) {
        UserMsg msg = userMsgService.getById(id);
        if (msg == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "要删除的消息不存在", false);
        }
        // 试图删除别人的消息，为了安全性，此时直接返回消息不存在
        if (!msg.getSendUser().equals(LoginUtil.getUserId())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "要删除的消息不存在", false);
        }
        // 只有消息还是未读状态时才能删除
        if (UserMsgStatusEnum.UNREAD != msg.getStatus()) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "只有消息还是未读状态时才能删除", false);
        }

        boolean flag = false;
        try {
            flag = userMsgService.deleteUserMsg(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("删除用户消息发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 获取用户消息详情
     */
    @GetMapping("/{id}")
    @OperationLog(name = "用户消息详情", type = OperationLogType.INFO)
    @ApiOperation(value = "用户消息详情", response = UserMsgQueryVo.class)
    public ApiResult<UserMsgQueryVo> getUserMsg(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            UserMsgQueryVo userMsgQueryVo = userMsgService.getUserMsgById(id);
            Long currentUserId = LoginUtil.getUserId();
            // 消息的接收用户还有发送用户才能查看消息，为了更加安全，如果不符合，直接回消息不存在
            if (!userMsgQueryVo.getSendUser().equals(currentUserId)
                    && !userMsgQueryVo.getReceiveUser().equals(currentUserId)) {
                response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "要查看的消息不存在", null);
            }
            response.setStatus(HttpServletResponse.SC_OK);
            return ApiResult.ok(userMsgQueryVo);
        } catch (Exception e) {
            log.error("获取用户消息详情发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ApiResult.fail(ApiCode.SYSTEM_EXCEPTION, null);
        }
    }

    /**
     * 用户消息分页列表
     */
    @GetMapping("")
    @OperationLog(name = "用户消息分页列表", type = OperationLogType.PAGE)
    @ApiImplicitParam(paramType = "query", dataTypeClass = UserMsgPageParam.class)
    @ApiOperation(value = "用户消息分页列表", response = UserMsgQueryVo.class)
    public ApiResult<Paging<UserMsgQueryVo>> getUserMsgPageList(UserMsgPageParam userMsgPageParam, HttpServletResponse response) {
        // 只能查看自己发送和接收的消息
        Long currentUserId = LoginUtil.getUserId();
        userMsgPageParam.setSendUser(currentUserId);
        userMsgPageParam.setReceiveUser(currentUserId);

        try {
            Paging<UserMsgQueryVo> paging = userMsgService.getUserMsgPageList(userMsgPageParam);
            response.setStatus(HttpServletResponse.SC_OK);
            return ApiResult.ok(paging);
        } catch (Exception e) {
            log.error("获取用户消息分页列表发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ApiResult.fail(ApiCode.SYSTEM_EXCEPTION, null);
        }
    }


    /**
     * 用户消息分页列表
     */
    @GetMapping("/list1")
    @OperationLog(name = "用户消息分页列表", type = OperationLogType.PAGE)
    @ApiImplicitParam(paramType = "query", dataTypeClass = UserMsgPageParam.class)
    @ApiOperation(value = "用户消息分页列表", response = UserMsgQueryVo.class)
    public ApiResult<Paging<UserMsgQueryVo>> getUserMsgPageList2(@RequestBody UserMsgPageParam userMsgPageParam, HttpServletResponse response) {
        // 只能查看自己发送和接收的消息
        Long currentUserId = LoginUtil.getUserId();
        userMsgPageParam.setSendUser(currentUserId);
        userMsgPageParam.setReceiveUser(currentUserId);

        try {
            Paging<UserMsgQueryVo> paging = userMsgService.getUserMsgPageList(userMsgPageParam);
            response.setStatus(HttpServletResponse.SC_OK);
            return ApiResult.ok(paging);
        } catch (Exception e) {
            log.error("获取用户消息分页列表发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ApiResult.fail(ApiCode.SYSTEM_EXCEPTION, null);
        }
    }

}

