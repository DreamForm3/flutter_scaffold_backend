package io.geekidea.springbootplus.system.param;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotBlank;

@Data
@ApiModel("验证码参数")
public class VerificationCodeParam {

    @NotBlank
    @ApiModelProperty("验证码Token")
    private String verifyToken;

    @NotBlank
    @ApiModelProperty("验证码")
    private String code;

    @ApiModelProperty("接收验证码的邮箱或者手机号码")
    private String receiveClient;
}
