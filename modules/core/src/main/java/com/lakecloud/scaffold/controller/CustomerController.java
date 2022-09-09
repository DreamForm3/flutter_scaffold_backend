package com.lakecloud.scaffold.controller;

import com.lakecloud.scaffold.entity.Customer;
import com.lakecloud.scaffold.service.CustomerService;
import lombok.extern.slf4j.Slf4j;
import com.lakecloud.scaffold.param.CustomerPageParam;
import io.geekidea.springbootplus.framework.common.controller.BaseController;
import com.lakecloud.scaffold.vo.CustomerQueryVo;
import io.geekidea.springbootplus.framework.common.api.ApiResult;
import io.geekidea.springbootplus.framework.core.pagination.Paging;
import io.geekidea.springbootplus.framework.common.param.IdParam;
import io.geekidea.springbootplus.framework.common.api.ApiCode;
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
 * 客户 控制器
 *
 * @author Alex.King
 * @since 2020-10-28
 */
@Slf4j
@RestController
@RequestMapping("/customers")
@Module("scaffold")
@Api(value = "客户API", tags = {"客户"})
public class CustomerController extends BaseController {

    @Autowired
    private CustomerService customerService;

    /**
     * 添加客户
     */
    @PostMapping("")
    @OperationLog(name = "添加客户", type = OperationLogType.ADD)
    @ApiOperation(value = "添加客户", response = ApiResult.class)
    public ApiResult<Boolean> addCustomer(@Validated(Add.class) @RequestBody Customer customer, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = customerService.saveCustomer(customer);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("添加客户发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 修改客户
     */
    @PutMapping("/{id}")
    @OperationLog(name = "修改客户", type = OperationLogType.UPDATE)
    @ApiOperation(value = "修改客户", response = ApiResult.class)
    public ApiResult<Boolean> updateCustomer(@PathVariable("id") Long id, @Validated(Update.class) @RequestBody Customer customer, HttpServletResponse response) {
        boolean flag = false;
        try {
            customer.setId(id);
            flag = customerService.updateCustomer(customer);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("修改客户发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 删除客户
     */
    @DeleteMapping("/{id}")
    @OperationLog(name = "删除客户", type = OperationLogType.DELETE)
    @ApiOperation(value = "删除客户", response = ApiResult.class)
    public ApiResult<Boolean> deleteCustomer(@PathVariable("id") Long id, HttpServletResponse response) {
        boolean flag = false;
        try {
            flag = customerService.deleteCustomer(id);
            response.setStatus(HttpServletResponse.SC_OK);
        } catch (Exception e) {
            log.error("删除客户发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
        }
        return ApiResult.result(flag);
    }

    /**
     * 获取客户详情
     */
    @GetMapping("/{id}")
    @OperationLog(name = "客户详情", type = OperationLogType.INFO)
    @ApiOperation(value = "客户详情", response = CustomerQueryVo.class)
    public ApiResult<CustomerQueryVo> getCustomer(@PathVariable("id") Long id, HttpServletResponse response) {
        try {
            CustomerQueryVo customerQueryVo = customerService.getCustomerById(id);
            response.setStatus(HttpServletResponse.SC_OK);
            return ApiResult.ok(customerQueryVo);
        } catch (Exception e) {
            log.error("获取客户详情发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ApiResult.fail(ApiCode.SYSTEM_EXCEPTION, null);
        }
    }

    /**
     * 客户分页列表
     */
    @GetMapping("")
    @OperationLog(name = "客户分页列表", type = OperationLogType.PAGE)
    @ApiImplicitParam(paramType = "query", dataTypeClass = CustomerPageParam.class)
    @ApiOperation(value = "客户分页列表", response = CustomerQueryVo.class)
    public ApiResult<Paging<CustomerQueryVo>> getCustomerPageList(CustomerPageParam customerPageParam, HttpServletResponse response) {
        try {
            Paging<CustomerQueryVo> paging = customerService.getCustomerPageList(customerPageParam);
            response.setStatus(HttpServletResponse.SC_OK);
            return ApiResult.ok(paging);
        } catch (Exception e) {
            log.error("获取客户分页列表发生异常", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ApiResult.fail(ApiCode.SYSTEM_EXCEPTION, null);
        }
    }

}

