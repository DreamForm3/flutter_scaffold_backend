package com.example.order.controller;

import com.example.order.entity.ExampleOrder;
import com.example.order.service.ExampleOrderService;
import lombok.extern.slf4j.Slf4j;
import com.example.order.param.ExampleOrderPageParam;
import io.geekidea.springbootplus.framework.common.controller.BaseController;
import com.example.order.vo.ExampleOrderQueryVo;
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
 * 订单示例 控制器
 *
 * @author Alex.King
 * @since 2020-09-17
 */
@Slf4j
@RestController
@RequestMapping("/exampleOrders")
@Module("order")
@Api(value = "订单示例API", tags = {"订单示例"})
public class ExampleOrderController extends BaseController {

    @Autowired
    private ExampleOrderService exampleOrderService;

    /**
     * 添加订单示例
     */
    @PostMapping("")
    @OperationLog(name = "添加订单示例", type = OperationLogType.ADD)
    @ApiOperation(value = "添加订单示例", response = ApiResult.class)
    public ApiResult<Boolean> addExampleOrder(@Validated(Add.class) @RequestBody ExampleOrder exampleOrder, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = exampleOrderService.saveExampleOrder(exampleOrder);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("添加订单示例发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 修改订单示例
     */
    @PutMapping("/{id}")
    @OperationLog(name = "修改订单示例", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改订单示例", response = ApiResult.class)
    public ApiResult<Boolean> updateExampleOrder(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody ExampleOrder exampleOrder, HttpServletResponse response) {
        boolean flag = false;
        try {
            exampleOrder.setId(id);
            flag = exampleOrderService.updateExampleOrder(exampleOrder);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("修改订单示例发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 删除订单示例
     */
    @DeleteMapping("/{id}")
    @OperationLog(name = "删除订单示例", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除订单示例", response = ApiResult.class)
    public ApiResult<Boolean> deleteExampleOrder(@PathVariable("id") Long id, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = exampleOrderService.deleteExampleOrder(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("删除订单示例发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 获取订单示例详情
     */
    @GetMapping("/{id}")
    @OperationLog(name = "订单示例详情", type = OperationLogType.INFO)
    @ApiOperation(value = "订单示例详情", response = ExampleOrderQueryVo.class)
    public ApiResult<ExampleOrderQueryVo> getExampleOrder(@PathVariable("id") Long id, HttpServletResponse response) {
        ExampleOrderQueryVo exampleOrderQueryVo = null;
        try {
            exampleOrderQueryVo = exampleOrderService.getExampleOrderById(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("获取订单示例详情发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(exampleOrderQueryVo);
    }

    /**
     * 订单示例分页列表
     */
    @GetMapping("")
    @OperationLog(name = "订单示例分页列表", type = OperationLogType.PAGE)
    @ApiImplicitParam(paramType = "query", dataTypeClass = ExampleOrderPageParam.class)
    @ApiOperation(value = "订单示例分页列表", response = ExampleOrderQueryVo.class)
    public ApiResult<Paging<ExampleOrderQueryVo>> getExampleOrderPageList(ExampleOrderPageParam exampleOrderPageParam, HttpServletResponse response) {
        Paging<ExampleOrderQueryVo> paging = new Paging<>();
        try {
            paging = exampleOrderService.getExampleOrderPageList(exampleOrderPageParam);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("获取订单示例分页列表发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.ok(paging);
    }

}

