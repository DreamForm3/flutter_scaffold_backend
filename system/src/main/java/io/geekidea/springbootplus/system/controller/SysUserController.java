/*
 * Copyright 2019-2029 geekidea(https://github.com/geekidea)
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package io.geekidea.springbootplus.system.controller;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONException;
import io.geekidea.springbootplus.config.properties.SpringBootPlusProperties;
import io.geekidea.springbootplus.framework.common.api.ApiResult;
import io.geekidea.springbootplus.framework.common.controller.BaseController;
import io.geekidea.springbootplus.framework.common.exception.BusinessException;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import io.geekidea.springbootplus.framework.core.validator.groups.Add;
import io.geekidea.springbootplus.framework.core.validator.groups.Update;
import io.geekidea.springbootplus.framework.log.annotation.Module;
import io.geekidea.springbootplus.framework.log.annotation.OperationLog;
import io.geekidea.springbootplus.framework.log.enums.OperationLogType;
import io.geekidea.springbootplus.framework.util.LoginUtil;
import io.geekidea.springbootplus.framework.util.UploadUtil;
import io.geekidea.springbootplus.system.entity.SysUser;
import io.geekidea.springbootplus.system.param.sysuser.ResetPasswordParam;
import io.geekidea.springbootplus.system.param.sysuser.SysUserPageParam;
import io.geekidea.springbootplus.system.param.sysuser.UpdatePasswordParam;
import io.geekidea.springbootplus.system.param.sysuser.UploadHeadParam;
import io.geekidea.springbootplus.system.service.SysUserService;
import io.geekidea.springbootplus.system.vo.SysUserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.Base64Utils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;

/**
 * 系统用户 控制器
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Slf4j
@RestController
@RequestMapping("/sysUsers")
@Module("system")
@Api(value = "系统用户API", tags = {"系统用户"})
public class SysUserController extends BaseController {

    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SpringBootPlusProperties springBootPlusProperties;

    /**
     * 添加系统用户
     */
    @PostMapping("")
    @OperationLog(name = "添加系统用户", type = OperationLogType.ADD)
    @ApiOperation(value = "添加系统用户", response = ApiResult.class)
    public ApiResult<Boolean> addSysUser(@Validated(Add.class) @RequestBody SysUser sysUser, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = sysUserService.saveSysUser(sysUser);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("添加系统用户发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 修改系统用户
     */
    @PutMapping("/{id}")
    @OperationLog(name = "修改系统用户", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改系统用户", response = ApiResult.class)
    public ApiResult<Boolean> updateSysUser(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody SysUser sysUser, HttpServletResponse response) {
        boolean flag = false;
        try {
            sysUser.setId(id);
            flag = sysUserService.updateSysUser(sysUser);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("修改系统用户发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 删除系统用户
     */
    @DeleteMapping("/{id}")
    @OperationLog(name = "删除系统用户", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除系统用户", response = ApiResult.class)
    public ApiResult<Boolean> deleteSysUser(@PathVariable("id") Long id, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = sysUserService.deleteSysUser(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("删除系统用户发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 获取系统用户详情
     */
    @GetMapping("/{id}")
    @OperationLog(name = "系统用户详情", type = OperationLogType.INFO)
    @ApiOperation(value = "系统用户详情", response = SysUserQueryVo.class)
    public ApiResult<SysUserQueryVo> getSysUser(@PathVariable("id") Long id, HttpServletResponse response) {
        SysUserQueryVo sysUserQueryVo = null;
        try {
            sysUserQueryVo = sysUserService.getSysUserById(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("获取系统用户详情发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(sysUserQueryVo);
    }

    /**
     * 系统用户分页列表
     */
    @GetMapping("")
    @OperationLog(name = "系统用户分页列表", type = OperationLogType.PAGE)
    @ApiImplicitParam(paramType = "query", dataTypeClass = SysUserPageParam.class)
    @ApiOperation(value = "系统用户分页列表", response = SysUserQueryVo.class)
    public ApiResult<Paging<SysUserQueryVo>> getSysUserPageList(SysUserPageParam sysUserPageParam, HttpServletResponse response) {
        Paging<SysUserQueryVo> paging = new Paging<>();
        try {
            paging = sysUserService.getSysUserPageList(sysUserPageParam);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("获取系统用户分页列表发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(paging);
    }

    /**
     * 修改密码
     */
    @PostMapping("/updatePassword")
    @OperationLog(name = "修改密码", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改密码", response = ApiResult.class)
    public ApiResult<Boolean> updatePassword(@Validated @RequestBody UpdatePasswordParam updatePasswordParam, HttpServletResponse response) throws Exception {
        boolean flag = false;
        int responseCode = HttpServletResponse.SC_OK;
        String responseMsg = null;
        try {
            flag = sysUserService.updatePassword(updatePasswordParam);
        } catch (BusinessException e) {
            responseCode = HttpServletResponse.SC_BAD_REQUEST;
            response.setStatus(responseCode);
            responseMsg = e.getMessage();
        } catch (Exception e) {
            responseCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            response.setStatus(responseCode);
            responseMsg = "修改密码发生异常";
            log.error(responseMsg, e);
        }
        return ApiResult.result(responseCode, responseMsg, flag);
    }

    /**
     * 管理员重置用户密码
     */
    @PostMapping("/resetPassword")
    @RequiresPermissions("sys:user:reset:password")
    @OperationLog(name = "管理员重置用户密码", type = OperationLogType.UPDATE)
    @ApiOperation(value = "管理员重置用户密码", response = ApiResult.class)
    public ApiResult<Boolean> resetPassword(@Validated @RequestBody ResetPasswordParam resetPasswordParam) throws Exception {
        boolean flag = sysUserService.resetPassword(resetPasswordParam);
        return ApiResult.result(flag);
    }

    /**
     * 修改头像
     */
    @PostMapping("/uploadAvatar")
    @RequiresPermissions("sys:user:update:avatar")
    @OperationLog(name = "修改头像", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改头像", response = ApiResult.class)
    public ApiResult<Boolean> uploadHead(@Validated @RequestBody UploadHeadParam uploadHeadParam) throws Exception {
        boolean flag = sysUserService.updateSysUserHead(uploadHeadParam.getId(), uploadHeadParam.getAvatar());
        return ApiResult.result(flag);
    }

    @PutMapping("/updateProfile")
    @OperationLog(name = "用户更新用户资料", type = OperationLogType.UPDATE)
    @ApiOperation(value = "用户更新用户资料", response = ApiResult.class)
    public ApiResult<SysUserQueryVo> updateProfile(@RequestParam(value = "avatarFile", required = false) MultipartFile multipartFile,
                                            @RequestParam("sysUserBase64Json") String sysUserBase64Json,
                                            HttpServletResponse response) {
        boolean flag = false;
        String errorMsg;
        int errorCode;
        try {
            // 前端传输的是BASE64编码后的json，需要先解析成原始json
            String sysUserJson = new String(Base64Utils.decodeFromString(sysUserBase64Json));
            SysUser user = JSON.parseObject(sysUserJson, SysUser.class);
            // 从当前登陆用户获取用户ID，用户只能更新自己的用户信息
            user.setId(LoginUtil.getUserId());
            // 上传新的用户头像并且获取访问路径
            if (multipartFile != null) {
                String saveFileName = UploadUtil.upload(springBootPlusProperties.getUploadPath(), multipartFile);
                String fileAccessPath = springBootPlusProperties.getResourceAccessUrl() + saveFileName;
                user.setAvatar(fileAccessPath);
            }
            // 更新用户信息
            flag = sysUserService.updateSysUser(user);
            // 获取最新的用户信息并返回
            SysUserQueryVo sysUserQueryVo = sysUserService.getSysUserById(user.getId());
            response.setStatus(HttpServletResponse.SC_OK);
            return ApiResult.ok(sysUserQueryVo);
        }
        catch (JSONException e) {
            errorMsg = "用户资料格式不正确，无法解析";
            errorCode = HttpServletResponse.SC_BAD_REQUEST;
            log.warn("更新用户信息发生错误，" + errorMsg, e);
            response.setStatus(errorCode);
        }
        catch (Exception e) {
            errorMsg = "更新用户信息发生异常";
            errorCode = HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
            log.error(errorMsg, e);
            response.setStatus(errorCode);
        }
        return ApiResult.result(errorCode, errorMsg, null);
    }
}

