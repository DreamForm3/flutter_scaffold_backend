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
import io.geekidea.springbootplus.system.entity.SysDepartment;
import io.geekidea.springbootplus.system.param.SysDepartmentPageParam;
import io.geekidea.springbootplus.system.service.SysDepartmentService;
import io.geekidea.springbootplus.system.vo.SysDepartmentQueryVo;
import io.geekidea.springbootplus.system.vo.SysDepartmentTreeVo;
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
 * ?????? ?????????
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Slf4j
@RestController
@RequestMapping("/sysDepartments")
@Module("system")
@Api(value = "??????API", tags = {"??????"})
public class SysDepartmentController extends BaseController {

    @Autowired
    private SysDepartmentService sysDepartmentService;

    /**
     * ????????????
     */
    @PostMapping("")
    @OperationLog(name = "????????????", type = OperationLogType.ADD)
    @ApiOperation(value = "????????????", response = ApiResult.class)
    public ApiResult<Boolean> addSysDepartment(@Validated(Add.class) @RequestBody SysDepartment sysDepartment, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = sysDepartmentService.saveSysDepartment(sysDepartment);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("????????????????????????", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * ????????????
     */
    @PutMapping("/{id}")
    @OperationLog(name = "????????????", type = OperationLogType.UPDATE)
    @ApiOperation(value = "????????????", response = ApiResult.class)
    public ApiResult<Boolean> updateSysDepartment(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody SysDepartment sysDepartment, HttpServletResponse response) {
        boolean flag = false;
        try {
            sysDepartment.setId(id);
            flag = sysDepartmentService.updateSysDepartment(sysDepartment);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("????????????????????????", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * ????????????
     */
    @DeleteMapping("/{id}")
    @OperationLog(name = "????????????", type = OperationLogType.DELETE)
    @ApiOperation(value = "????????????", response = ApiResult.class)
    public ApiResult<Boolean> deleteSysDepartment(@PathVariable("id") Long id, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = sysDepartmentService.deleteSysDepartment(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("????????????????????????", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * ??????????????????
     */
    @GetMapping("/{id}")
    @OperationLog(name = "????????????", type = OperationLogType.INFO)
    @ApiOperation(value = "????????????", response = SysDepartmentQueryVo.class)
    public ApiResult<SysDepartmentQueryVo> getSysDepartment(@PathVariable("id") Long id, HttpServletResponse response) {
        SysDepartmentQueryVo sysDepartmentQueryVo = null;
        try {
            sysDepartmentQueryVo = sysDepartmentService.getSysDepartmentById(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("??????????????????????????????", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(sysDepartmentQueryVo);
    }

    /**
     * ??????????????????
     */
    @GetMapping("")
    @OperationLog(name = "??????????????????", type = OperationLogType.PAGE)
    @ApiImplicitParam(paramType = "query", dataTypeClass = SysDepartmentPageParam.class)
    @ApiOperation(value = "??????????????????", response = SysDepartmentQueryVo.class)
    public ApiResult<Paging<SysDepartmentQueryVo>> getSysDepartmentPageList(SysDepartmentPageParam sysDepartmentPageParam, HttpServletResponse response) {
        Paging<SysDepartmentQueryVo> paging = new Paging<>();
        try {
            paging = sysDepartmentService.getSysDepartmentPageList(sysDepartmentPageParam);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("????????????????????????????????????", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(paging);
    }

    /**
     * ????????????????????????
     */
    @PostMapping("/getAllDepartmentList")
    @RequiresPermissions("sys:department:all:list")
    @OperationLog(name = "?????????????????????????????????", type = OperationLogType.OTHER_QUERY)
    @ApiOperation(value = "?????????????????????????????????", response = SysDepartment.class)
    public ApiResult<List<SysDepartment>> getAllDepartmentList() throws Exception {
        List<SysDepartment> list = sysDepartmentService.getAllDepartmentList();
        return ApiResult.ok(list);
    }

    /**
     * ?????????????????????????????????
     *
     * @return
     */
    @PostMapping("/getDepartmentTree")
    @RequiresPermissions("sys:department:all:tree")
    @OperationLog(name = "?????????????????????????????????", type = OperationLogType.OTHER_QUERY)
    @ApiOperation(value = "?????????????????????????????????", response = SysDepartmentTreeVo.class)
    public ApiResult<List<SysDepartmentTreeVo>> getDepartmentTree() throws Exception {
        List<SysDepartmentTreeVo> treeVos = sysDepartmentService.getDepartmentTree();
        return ApiResult.ok(treeVos);
    }

    /**
     * ????????????
     */
    @PostMapping("/getList")
    @RequiresPermissions("sys:department:list")
    @OperationLog(name = "????????????", type = OperationLogType.LIST)
    @ApiOperation(value = "????????????", response = SysDepartment.class)
    public ApiResult<List<SysDepartment>> getSysDepartmentList() throws Exception {
        List<SysDepartment> list = sysDepartmentService.list();
        return ApiResult.ok(list);
    }

}

