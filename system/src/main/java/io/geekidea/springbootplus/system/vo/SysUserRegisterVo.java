package io.geekidea.springbootplus.system.vo;

import io.geekidea.springbootplus.system.entity.SysUser;
import io.geekidea.springbootplus.system.param.VerificationCodeParam;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * <pre>
 * 客户 查询结果对象
 * </pre>
 *
 * @author Alex.King
 */
@Data
@Accessors(chain = true)
@ApiModel(value = "SysUserRegisterVo对象", description = "系统用户注册对象")
public class SysUserRegisterVo {
    @ApiModelProperty("验证码")
    private VerificationCodeParam verificationCodeParam;
    @ApiModelProperty("用户信息")
    private SysUser sysUser;
}
