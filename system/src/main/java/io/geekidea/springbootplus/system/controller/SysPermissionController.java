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
import io.geekidea.springbootplus.system.entity.SysPermission;
import io.geekidea.springbootplus.system.param.SysPermissionPageParam;
import io.geekidea.springbootplus.system.service.SysPermissionService;
import io.geekidea.springbootplus.system.service.SysRolePermissionService;
import io.geekidea.springbootplus.system.vo.SysPermissionQueryVo;
import io.geekidea.springbootplus.system.vo.SysPermissionTreeVo;
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
 * 系统权限资源 控制器
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Slf4j
@RestController
@RequestMapping("/sysPermissions")
@Module("system")
@Api(value = "系统权限资源API", tags = {"系统权限资源"})
public class SysPermissionController extends BaseController {

    @Autowired
    private SysPermissionService sysPermissionService;
    @Autowired
    private SysRolePermissionService sysRolePermissionService;

    /**
     * 添加系统权限资源
     */
    @PostMapping("")
    @OperationLog(name = "添加系统权限资源", type = OperationLogType.ADD)
    @ApiOperation(value = "添加系统权限资源", response = ApiResult.class)
    public ApiResult<Boolean> addSysPermission(@Validated(Add.class) @RequestBody SysPermission sysPermission, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = sysPermissionService.saveSysPermission(sysPermission);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("添加系统权限资源发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 修改系统权限资源
     */
    @PutMapping("/{id}")
    @OperationLog(name = "修改系统权限资源", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改系统权限资源", response = ApiResult.class)
    public ApiResult<Boolean> updateSysPermission(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody SysPermission sysPermission, HttpServletResponse response) {
        boolean flag = false;
        try {
            sysPermission.setId(id);
            flag = sysPermissionService.updateSysPermission(sysPermission);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("修改系统权限资源发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 删除系统权限资源
     */
    @DeleteMapping("/{id}")
    @OperationLog(name = "删除系统权限资源", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除系统权限资源", response = ApiResult.class)
    public ApiResult<Boolean> deleteSysPermission(@PathVariable("id") Long id, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = sysPermissionService.deleteSysPermission(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("删除系统权限资源发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 获取系统权限资源详情
     */
    @GetMapping("/{id}")
    @OperationLog(name = "系统权限资源详情", type = OperationLogType.INFO)
    @ApiOperation(value = "系统权限资源详情", response = SysPermissionQueryVo.class)
    public ApiResult<SysPermissionQueryVo> getSysPermission(@PathVariable("id") Long id, HttpServletResponse response) {
        SysPermissionQueryVo sysPermissionQueryVo = null;
        try {
            sysPermissionQueryVo = sysPermissionService.getSysPermissionById(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("获取系统权限资源详情发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(sysPermissionQueryVo);
    }

    /**
     * 系统权限资源分页列表
     */
    @GetMapping("")
    @OperationLog(name = "系统权限资源分页列表", type = OperationLogType.PAGE)
    @ApiImplicitParam(paramType = "query", dataTypeClass = SysPermissionPageParam.class)
    @ApiOperation(value = "系统权限资源分页列表", response = SysPermissionQueryVo.class)
    public ApiResult<Paging<SysPermissionQueryVo>> getSysPermissionPageList(SysPermissionPageParam sysPermissionPageParam, HttpServletResponse response) {
        Paging<SysPermissionQueryVo> paging = new Paging<>();
        try {
            paging = sysPermissionService.getSysPermissionPageList(sysPermissionPageParam);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("获取系统权限资源分页列表发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(paging);
    }

    /**
     * 获取所有菜单列表
     * @return
     */
    @PostMapping("/getAllMenuList")
    @RequiresPermissions("sys:permission:all:menu:list")
    @OperationLog(name = "获取所有菜单列表", type = OperationLogType.LIST)
    @ApiOperation(value = "获取所有菜单列表", response = SysPermission.class)
    public ApiResult<List<SysPermission>> getAllMenuList() throws Exception {
        List<SysPermission> list = sysPermissionService.getAllMenuList();
        return ApiResult.ok(list);
    }

    /**
     * 获取获取菜单树形列表
     * @return
     */
    @PostMapping("/getAllMenuTree")
    @RequiresPermissions("sys:permission:all:menu:tree")
    @OperationLog(name = "获取获取菜单树形列表", type = OperationLogType.OTHER_QUERY)
    @ApiOperation(value = "获取获取菜单树形列表", response = SysPermissionTreeVo.class)
    public ApiResult<List<SysPermissionTreeVo>> getAllMenuTree() throws Exception {
        List<SysPermissionTreeVo> treeVos = sysPermissionService.getAllMenuTree();
        return ApiResult.ok(treeVos);
    }


    /**
     * 根据用户id获取菜单列表
     * @return
     */
    @PostMapping("/getMenuListByUserId/{userId}")
    @RequiresPermissions("sys:permission:menu:list")
    @OperationLog(name = "根据用户id获取菜单列表", type = OperationLogType.OTHER_QUERY)
    @ApiOperation(value = "根据用户id获取菜单列表", response = SysPermission.class)
    public ApiResult<List<SysPermission>> getMenuListByUserId(@PathVariable("userId") Long userId) throws Exception {
        List<SysPermission> list = sysPermissionService.getMenuListByUserId(userId);
        return ApiResult.ok(list);
    }

    /**
     * 根据用户id获取菜单树形列表
     * @return
     */
    @PostMapping("/getMenuTreeByUserId/{userId}")
    @RequiresPermissions("sys:permission:menu:tree")
    @OperationLog(name = "根据用户id获取菜单树形列表", type = OperationLogType.OTHER_QUERY)
    @ApiOperation(value = "根据用户id获取菜单树形列表", response = SysPermissionTreeVo.class)
    public ApiResult<List<SysPermissionTreeVo>> getMenuTreeByUserId(@PathVariable("userId") Long userId) throws Exception {
        List<SysPermissionTreeVo> treeVos = sysPermissionService.getMenuTreeByUserId(userId);
        return ApiResult.ok(treeVos);
    }

    /**
     * 根据用户id获取该用户所有权限编码
     * @return
     */
    @GetMapping("/getPermissionCodesByUserId/{userId}")
    @RequiresPermissions("sys:permission:codes")
    @OperationLog(name = "根据用户id获取该用户所有权限编码", type = OperationLogType.OTHER_QUERY)
    @ApiOperation(value = "根据用户id获取该用户所有权限编码", response = ApiResult.class)
    public ApiResult<List<String>> getPermissionCodesByUserId(@PathVariable("userId") Long userId) throws Exception {
        List<String> list = sysPermissionService.getPermissionCodesByUserId(userId);
        return ApiResult.ok(list);
    }

    /**
     * 根据角色id获取该对应的所有三级权限ID
     * @return
     */
    @GetMapping("/getThreeLevelPermissionIdsByRoleId/{roleId}")
    @RequiresPermissions("sys:permission:three-ids-by-role-id")
    @OperationLog(name = "根据角色id获取该对应的所有三级权限ID", type = OperationLogType.OTHER_QUERY)
    @ApiOperation(value = "根据角色id获取该对应的所有三级权限ID", response = ApiResult.class)
    public ApiResult<List<Long>> getPermissionIdsByRoleId(@PathVariable("roleId") Long roleId) throws Exception {
        List<Long> list = sysRolePermissionService.getThreeLevelPermissionIdsByRoleId(roleId);
        return ApiResult.ok(list);
    }

    /**
     * 获取所有导航树形菜单(一级/二级菜单)
     * @return
     */
    @PostMapping("/getNavMenuTree")
    @RequiresPermissions("sys:permission:nav-menu")
    @OperationLog(name = "获取所有导航菜单(一级/二级菜单)", type = OperationLogType.OTHER_QUERY)
    @ApiOperation(value = "获取所有导航菜单(一级/二级菜单)", response = ApiResult.class)
    public ApiResult<List<SysPermissionTreeVo>> getNavMenuTree() throws Exception {
        List<SysPermissionTreeVo> list = sysPermissionService.getNavMenuTree();
        return ApiResult.ok(list);
    }

}

