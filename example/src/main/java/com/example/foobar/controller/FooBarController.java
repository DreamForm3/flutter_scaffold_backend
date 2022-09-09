package com.example.foobar.controller;

import com.example.foobar.entity.FooBar;
import com.example.foobar.service.FooBarService;
import lombok.extern.slf4j.Slf4j;
import com.example.foobar.param.FooBarPageParam;
import io.geekidea.springbootplus.framework.common.controller.BaseController;
import com.example.foobar.vo.FooBarQueryVo;
import io.geekidea.springbootplus.framework.common.api.ApiResult;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import io.geekidea.springbootplus.framework.common.param.IdParam;
import io.geekidea.springbootplus.framework.log.annotation.Module;
import io.geekidea.springbootplus.framework.log.annotation.OperationLog;
import io.geekidea.springbootplus.framework.log.enums.OperationLogType;
import io.geekidea.springbootplus.framework.core.validator.groups.Add;
import io.geekidea.springbootplus.framework.core.validator.groups.Update;
import org.springframework.validation.annotation.Validated;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiImplicitParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import javax.servlet.http.HttpServletResponse;

/**
 * FooBar 控制器
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Slf4j
@RestController
@RequestMapping("/fooBars")
@Module("foobar")
@Api(value = "FooBarAPI", tags = {"FooBar"})
public class FooBarController extends BaseController {

    @Autowired
    private FooBarService fooBarService;

    /**
     * 添加FooBar
     */
    @PostMapping("")
    @OperationLog(name = "添加FooBar", type = OperationLogType.ADD)
    @ApiOperation(value = "添加FooBar", response = ApiResult.class)
    public ApiResult<Boolean> addFooBar(@Validated(Add.class) @RequestBody FooBar fooBar, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = fooBarService.saveFooBar(fooBar);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("添加FooBar发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 修改FooBar
     */
    @PutMapping("/{id}")
    @OperationLog(name = "修改FooBar", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改FooBar", response = ApiResult.class)
    public ApiResult<Boolean> updateFooBar(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody FooBar fooBar, HttpServletResponse response) {
        boolean flag = false;
        try {
            fooBar.setId(id);
            flag = fooBarService.updateFooBar(fooBar);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("修改FooBar发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 删除FooBar
     */
    @DeleteMapping("/{id}")
    @OperationLog(name = "删除FooBar", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除FooBar", response = ApiResult.class)
    public ApiResult<Boolean> deleteFooBar(@PathVariable("id") Long id, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = fooBarService.deleteFooBar(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("删除FooBar发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 获取FooBar详情
     */
    @GetMapping("/{id}")
    @OperationLog(name = "FooBar详情", type = OperationLogType.INFO)
    @ApiOperation(value = "FooBar详情", response = FooBarQueryVo.class)
    public ApiResult<FooBarQueryVo> getFooBar(@PathVariable("id") Long id, HttpServletResponse response) {
        FooBarQueryVo fooBarQueryVo = null;
        try {
            fooBarQueryVo = fooBarService.getFooBarById(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("获取FooBar详情发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(fooBarQueryVo);
    }

    /**
     * FooBar分页列表
     */
    @GetMapping("")
    @OperationLog(name = "FooBar分页列表", type = OperationLogType.PAGE)
    @ApiImplicitParam(paramType = "query", dataTypeClass = FooBarPageParam.class)
    @ApiOperation(value = "FooBar分页列表", response = FooBarQueryVo.class)
    public ApiResult<Paging<FooBarQueryVo>> getFooBarPageList(FooBarPageParam fooBarPageParam, HttpServletResponse response) {
        Paging<FooBarQueryVo> paging = new Paging<>();
        try {
            paging = fooBarService.getFooBarPageList(fooBarPageParam);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("获取FooBar分页列表发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(paging);
    }

}

