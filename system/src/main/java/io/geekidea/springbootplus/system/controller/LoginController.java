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

import com.alibaba.fastjson.JSON;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.geekidea.springbootplus.framework.common.api.ApiResult;
import io.geekidea.springbootplus.framework.common.exception.BusinessException;
import io.geekidea.springbootplus.framework.log.annotation.Module;
import io.geekidea.springbootplus.framework.log.annotation.OperationLog;
import io.geekidea.springbootplus.framework.log.annotation.OperationLogIgnore;
import io.geekidea.springbootplus.framework.log.enums.OperationLogType;
import io.geekidea.springbootplus.framework.shiro.util.JwtTokenUtil;
import io.geekidea.springbootplus.system.entity.SysUser;
import io.geekidea.springbootplus.system.exception.VerificationCodeException;
import io.geekidea.springbootplus.system.param.FindPasswordParam;
import io.geekidea.springbootplus.system.param.sysuser.ResetPasswordParam;
import io.geekidea.springbootplus.system.service.LoginService;
import io.geekidea.springbootplus.system.service.SysUserService;
import io.geekidea.springbootplus.system.service.VerificationCodeService;
import io.geekidea.springbootplus.system.vo.LoginSysUserTokenVo;
import io.geekidea.springbootplus.system.vo.SysUserQueryVo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * ???????????????
 *
 * @author geekidea
 * @date 2019-09-28
 * @since 1.3.0.RELEASE
 **/
@Slf4j
@RestController
@Module("system")
@Api(value = "????????????API", tags = {"????????????"})
public class LoginController {

    @Autowired
    private LoginService loginService;

    @Autowired
    private SysUserService sysUserService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private VerificationCodeService verificationCodeService;

    @PostMapping("/login")
    @OperationLogIgnore
    @ApiOperation(value = "??????", notes = "??????????????????", response = io.geekidea.springbootplus.system.vo.LoginSysUserTokenVo.class)
    public ApiResult<LoginSysUserTokenVo> login(@Validated @RequestBody io.geekidea.springbootplus.system.param.LoginParam loginParam, HttpServletResponse response) throws Exception {
        LoginSysUserTokenVo loginSysUserTokenVo = loginService.login(loginParam);
        // ??????token?????????
        response.setHeader(JwtTokenUtil.getTokenName(), loginSysUserTokenVo.getToken());
        return ApiResult.ok(loginSysUserTokenVo, "????????????");
    }


    /**
     * ??????token??????????????????????????????
     *
     * @return
     */
    @GetMapping("/getSysUserInfo")
    @ApiOperation(value = "??????token??????????????????????????????", response = SysUserQueryVo.class)
    public ApiResult<JSON> getSysUser() throws Exception {
//        String token =  JwtTokenUtil.getToken();
//        String tokenSha256 = DigestUtils.sha256Hex(token);
//        LoginSysUserVo loginSysUserVo = (LoginSysUserVo) redisTemplate.opsForValue().get(tokenSha256);
//        return ApiResult.ok(loginSysUserVo);

        String json = "{\n" +
                "    roles: ['admin'],\n" +
                "    introduction: 'I am a super administrator',\n" +
                "    avatar: 'https://wpimg.wallstcn.com/f778738c-e4f8-4870-b634-56703b4acafe.gif',\n" +
                "    name: 'Super Admin'\n" +
                "  }";
        JSON array = JSON.parseObject(json);

        return ApiResult.ok(array);
    }

    @PostMapping("/logout")
    @OperationLogIgnore
    public ApiResult<String> logout(HttpServletRequest request) throws Exception {
        loginService.logout(request);
        return ApiResult.ok("????????????");
    }

    /**
     * ????????????????????????
     */
    @PostMapping("/findPassword")
    @OperationLog(name = "????????????????????????", type = OperationLogType.UPDATE)
    @ApiOperation(value = "????????????????????????", response = ApiResult.class)
    public ApiResult<Boolean> resetPassword(@Validated @RequestBody FindPasswordParam findPasswordParam, HttpServletResponse response) throws Exception {
        SysUser user = sysUserService.getOne(new QueryWrapper<SysUser>((SysUser) new SysUser()
                .setUsername(findPasswordParam.getUsername())
                .setIsDelete(false)));
        if (user == null) {
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "???????????????????????????", null);
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "??????????????????????????????????????????????????????", null);
        }
        // ???????????????
        try {
            verificationCodeService.checkVerifyCode(findPasswordParam.getVerifyToken(), findPasswordParam.getCode(), user.getEmail());
        } catch (VerificationCodeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), false);
        }
        // ????????????????????????????????????
        boolean flag = false;
        try {
            flag = sysUserService.resetPassword(new ResetPasswordParam()
                    .setUserId(user.getId())
                    .setNewPassword(findPasswordParam.getNewPassword())
                    .setConfirmPassword(findPasswordParam.getConfirmPassword()));
        } catch (BusinessException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, e.getMessage(), false);
        }
        return ApiResult.result(flag);
    }
}
