package io.geekidea.springbootplus.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotEmpty;

@Data
@ApiModel("找回密码参数")
public class FindPasswordParam {

    @NotEmpty(message = "用户名不能为空")
    @ApiModelProperty("用户名")
    private String username;

    @NotEmpty(message = "验证码Token不能为空")
    @ApiModelProperty("验证码Token")
    private String verifyToken;

    @NotEmpty(message = "验证码不能为空")
    @ApiModelProperty("验证码")
    private String code;

    @ApiModelProperty("新密码")
    @NotEmpty(message = "新密码不能为空")
    private String newPassword;

    @ApiModelProperty("新密码")
    @NotEmpty(message = "确认密码不能为空")
    private String confirmPassword;
}
