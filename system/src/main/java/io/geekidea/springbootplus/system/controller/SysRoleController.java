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

import io.geekidea.springbootplus.framework.common.api.ApiResult;
import io.geekidea.springbootplus.framework.common.controller.BaseController;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import io.geekidea.springbootplus.framework.core.validator.groups.Add;
import io.geekidea.springbootplus.framework.core.validator.groups.Update;
import io.geekidea.springbootplus.framework.log.annotation.Module;
import io.geekidea.springbootplus.framework.log.annotation.OperationLog;
import io.geekidea.springbootplus.framework.log.enums.OperationLogType;
import io.geekidea.springbootplus.system.entity.SysRole;
import io.geekidea.springbootplus.system.param.sysrole.SysRolePageParam;
import io.geekidea.springbootplus.system.param.sysrole.UpdateSysRolePermissionParam;
import io.geekidea.springbootplus.system.service.SysRoleService;
import io.geekidea.springbootplus.system.vo.SysRoleQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiImplicitParam;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.shiro.authz.annotation.RequiresPermissions;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.util.List;

/**
 * ???????????? ?????????
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Slf4j
@RestController
@RequestMapping("/sysRoles")
@Module("system")
@Api(value = "????????????API", tags = {"????????????"})
public class SysRoleController extends BaseController {

    @Autowired
    private SysRoleService sysRoleService;

    /**
     * ??????????????????
     */
    @PostMapping("")
    @OperationLog(name = "??????????????????", type = OperationLogType.ADD)
    @ApiOperation(value = "??????????????????", response = ApiResult.class)
    public ApiResult<Boolean> addSysRole(@Validated(Add.class) @RequestBody SysRole sysRole, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = sysRoleService.saveSysRole(sysRole);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("??????????????????????????????", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * ??????????????????
     */
    @PutMapping("/{id}")
    @OperationLog(name = "??????????????????", type = OperationLogType.UPDATE)
    @ApiOperation(value = "??????????????????", response = ApiResult.class)
    public ApiResult<Boolean> updateSysRole(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody SysRole sysRole, HttpServletResponse response) {
        boolean flag = false;
        try {
            sysRole.setId(id);
            flag = sysRoleService.updateSysRole(sysRole);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("??????????????????????????????", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * ??????????????????
     */
    @DeleteMapping("/{id}")
    @OperationLog(name = "??????????????????", type = OperationLogType.DELETE)
    @ApiOperation(value = "??????????????????", response = ApiResult.class)
    public ApiResult<Boolean> deleteSysRole(@PathVariable("id") Long id, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = sysRoleService.deleteSysRole(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("??????????????????????????????", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * ????????????????????????
     */
    @GetMapping("/{id}")
    @OperationLog(name = "??????????????????", type = OperationLogType.INFO)
    @ApiOperation(value = "??????????????????", response = SysRoleQueryVo.class)
    public ApiResult<SysRoleQueryVo> getSysRole(@PathVariable("id") Long id, HttpServletResponse response) {
        SysRoleQueryVo sysRoleQueryVo = null;
        try {
            sysRoleQueryVo = sysRoleService.getSysRoleById(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("????????????????????????????????????", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(sysRoleQueryVo);
    }

    /**
     * ????????????????????????
     */
    @GetMapping("")
    @OperationLog(name = "????????????????????????", type = OperationLogType.PAGE)
    @ApiImplicitParam(paramType = "query", dataTypeClass = SysRolePageParam.class)
    @ApiOperation(value = "????????????????????????", response = SysRoleQueryVo.class)
    public ApiResult<Paging<SysRole>> getSysRolePageList(SysRolePageParam sysRolePageParam, HttpServletResponse response) {
        Paging<SysRole> paging = new Paging<>();
        try {
            paging = sysRoleService.getSysRolePageList(sysRolePageParam);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("??????????????????????????????????????????", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(paging);
    }

    /**
     * ????????????????????????
     *
     * @return
     */
    @PostMapping("/getList")
    @RequiresPermissions("sys:role:list")
    @OperationLog(name = "??????????????????", type = OperationLogType.LIST)
    @ApiOperation(value = "??????????????????", response = SysRole.class)
    public ApiResult<List<SysRole>> getRoleList() {
        return ApiResult.ok(sysRoleService.list());
    }

    /**
     * ????????????????????????
     */
    @PostMapping("/updateSysRolePermission")
    @RequiresPermissions("sys:role-permission:update")
    @OperationLog(name = "????????????????????????", type = OperationLogType.UPDATE)
    @ApiOperation(value = "????????????????????????", response = ApiResult.class)
    public ApiResult<Boolean> updateSysRolePermission(@Validated @RequestBody UpdateSysRolePermissionParam param) throws Exception {
        boolean flag = sysRoleService.updateSysRolePermission(param);
        return ApiResult.result(flag);
    }

}

