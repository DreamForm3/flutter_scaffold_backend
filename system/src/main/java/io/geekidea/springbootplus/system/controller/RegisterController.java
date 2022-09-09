package io.geekidea.springbootplus.system.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.geekidea.springbootplus.framework.common.api.ApiCode;
import io.geekidea.springbootplus.framework.common.api.ApiResult;
import io.geekidea.springbootplus.framework.common.controller.BaseController;
import io.geekidea.springbootplus.framework.common.exception.BusinessException;
import io.geekidea.springbootplus.framework.core.validator.groups.Add;
import io.geekidea.springbootplus.framework.log.annotation.Module;
import io.geekidea.springbootplus.framework.log.annotation.OperationLog;
import io.geekidea.springbootplus.framework.log.enums.OperationLogType;
import io.geekidea.springbootplus.system.entity.SysDepartment;
import io.geekidea.springbootplus.system.entity.SysRole;
import io.geekidea.springbootplus.system.entity.SysUser;
import io.geekidea.springbootplus.system.exception.VerificationCodeException;
import io.geekidea.springbootplus.system.param.VerificationCodeParam;
import io.geekidea.springbootplus.system.service.SysDepartmentService;
import io.geekidea.springbootplus.system.service.SysRoleService;
import io.geekidea.springbootplus.system.service.SysUserService;
import io.geekidea.springbootplus.system.service.VerificationCodeService;
import io.geekidea.springbootplus.system.vo.SysUserRegisterVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


/**
 * 注册 控制器
 *
 * @author Alex.King
 * @since 2020-10-28
 */
@Slf4j
@RestController
@RequestMapping("/register")
@Module("scaffold")
@Api(value = "注册API", tags = {"注册"})
public class RegisterController extends BaseController {

    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysRoleService sysRoleService;
    @Autowired
    private SysDepartmentService sysDepartmentService;

    /**
     * 添加客户
     */
    @PostMapping("")
    @OperationLog(name = "用户注册", type = OperationLogType.ADD)
    @ApiOperation(value = "用户注册", response = ApiResult.class)
    public ApiResult<String> addCustomer(@Validated(Add.class) @RequestBody SysUserRegisterVo registerVo, HttpServletResponse response) {
        if (registerVo.getSysUser() == null || registerVo.getVerificationCodeParam() == null) {
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "缺少必填参数", "请求数据缺少必填参数");
        }

        VerificationCodeParam verificationCodeParam = registerVo.getVerificationCodeParam();
        if (StringUtils.isBlank(verificationCodeParam.getVerifyToken())
                || StringUtils.isBlank(verificationCodeParam.getCode())
                || StringUtils.isBlank(verificationCodeParam.getReceiveClient())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(ApiCode.VERIFICATION_CODE_EXCEPTION.getCode(), "验证码校验失败",
                    "缺少验证码必要参数");
        }

        // 校验验证码
        try {
            verificationCodeService.checkVerifyCode(verificationCodeParam.getVerifyToken(), verificationCodeParam.getCode(), verificationCodeParam.getReceiveClient());
        } catch (VerificationCodeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.fail(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }

        StringBuilder errorSB = new StringBuilder();
        SysUser user = registerVo.getSysUser();
        if (StringUtils.isEmpty(user.getUsername())) {
            errorSB.append("用户名不能为空；");
        }
        if (StringUtils.isEmpty(user.getPassword())) {
            errorSB.append("密码不能为空；");
        }
        if (StringUtils.isEmpty(user.getNickname())) {
            errorSB.append("昵称不能为空；");
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            errorSB.append("邮箱不能为空；");
        }

        if (errorSB.length() > 0) {
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "缺少必填参数", errorSB.toString());
        }

        try {
            // 获取用户注册默认的角色、部门
            List<SysRole> roleList = sysRoleService.list(new QueryWrapper<SysRole>().setEntity(new SysRole().setCode("users")));
            user.setRoleId(roleList.get(0).getId());
            List<SysDepartment> departmentList = sysDepartmentService.list(new QueryWrapper<SysDepartment>().setEntity(new SysDepartment().setName("普通用户")));
            user.setDepartmentId(departmentList.get(0).getId());
            sysUserService.saveSysUser(user);
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.fail(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        } catch (Exception e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.fail(HttpServletResponse.SC_BAD_REQUEST, "注册失败");
        }
        return ApiResult.ok("注册成功");
    }



}

