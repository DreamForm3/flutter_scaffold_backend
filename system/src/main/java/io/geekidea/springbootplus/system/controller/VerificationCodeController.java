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

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import io.geekidea.springbootplus.config.constant.CommonConstant;
import io.geekidea.springbootplus.framework.common.api.ApiResult;
import io.geekidea.springbootplus.framework.log.annotation.Module;
import io.geekidea.springbootplus.framework.log.annotation.OperationLog;
import io.geekidea.springbootplus.framework.log.enums.OperationLogType;
import io.geekidea.springbootplus.framework.util.TokenUtil;
import io.geekidea.springbootplus.framework.util.UUIDUtil;
import io.geekidea.springbootplus.framework.util.VerificationCode;
import io.geekidea.springbootplus.system.entity.SysUser;
import io.geekidea.springbootplus.system.exception.VerificationCodeException;
import io.geekidea.springbootplus.system.param.VerificationCodeParam;
import io.geekidea.springbootplus.system.service.EmailService;
import io.geekidea.springbootplus.system.service.SysUserService;
import io.geekidea.springbootplus.system.service.VerificationCodeService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import javax.imageio.ImageIO;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServletResponse;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.util.Base64;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * 验证码接口
 *
 * @author geekidea
 * @date 2019-10-27
 **/
@Slf4j
@Controller
@Api(value = "验证码API", tags = {"验证码"})
@Module("system")
@RequestMapping("/verificationCode")
/*@ConditionalOnProperty(value = {"spring-boot-plus.enable-verify-code"}, matchIfMissing = true)*/
public class VerificationCodeController {

    /**
     * 验证码默认 5 分钟过期
     */
    @Value("${flutter-scaffold.verification-code.timeout:5}")
    private Integer verificationCodeTimeout;
    @Value("${flutter-scaffold.verification-code.email-suffix:}")
    private String verificationCodeEmailSuffix;

    @Autowired
    private StringRedisTemplate stringRedisTemplate;
    @Autowired
    private EmailService emailService;
    @Autowired
    private VerificationCodeService verificationCodeService;
    @Autowired
    private SysUserService userService;

    /**
     * 获取验证码
     */
    @GetMapping("/getImage")
    @OperationLog(name = "获取验证码", type = OperationLogType.OTHER)
    @ApiOperation(value = "获取验证码", response = ApiResult.class)
    public void getImage(HttpServletResponse response) throws Exception {
        VerificationCode verificationCode = new VerificationCode();
        BufferedImage image = verificationCode.getImage();
        String code = verificationCode.getText();
        String verifyToken = UUIDUtil.getUuid();
        // 缓存到Redis
        stringRedisTemplate.opsForValue().set(TokenUtil.getRedisKey(verifyToken, null), code, verificationCodeTimeout, TimeUnit.MINUTES);
        response.setHeader(CommonConstant.VERIFY_TOKEN, verifyToken);
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expire", 0);
        ServletOutputStream outputStream = response.getOutputStream();
        ImageIO.write(image, CommonConstant.JPEG, outputStream);
    }

    /**
     * 获取图片Base64验证码
     */
    @GetMapping("/getBase64Image")
    @ResponseBody
    @OperationLog(name = "获取图片Base64验证码", type = OperationLogType.OTHER)
    @ApiOperation(value = "获取图片Base64验证码", response = ApiResult.class)
    public ApiResult<Map<String, Object>> getCode(HttpServletResponse response) throws Exception {
        VerificationCode verificationCode = new VerificationCode();
        BufferedImage image = verificationCode.getImage();
        String code = verificationCode.getText();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, CommonConstant.JPEG, outputStream);
        // 将图片转换成base64字符串
        String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        // 生成当前验证码会话token
        String verifyToken = UUIDUtil.getUuid();
        Map<String, Object> map = new HashMap<>(2);
        map.put(CommonConstant.IMAGE, CommonConstant.BASE64_PREFIX + base64);
        map.put(CommonConstant.VERIFY_TOKEN, verifyToken);
        // 缓存到Redis
        stringRedisTemplate.opsForValue().set(TokenUtil.getRedisKey(verifyToken, null), code, verificationCodeTimeout, TimeUnit.MINUTES);
        return ApiResult.ok(map);
    }

    /**
     * 根据用户名获取邮箱验证码
     * @param username 接收验证码的用户名
     * @return
     */
    @GetMapping("/username")
    @ResponseBody
    @OperationLog(name = "根据用户名获取邮箱验证码", type = OperationLogType.OTHER)
    @ApiOperation(value = "根据用户名获取邮箱验证码", response = ApiResult.class)
    public ApiResult<Map<String, Object>> getEmailCodeByUsername(@RequestParam("username") String username, HttpServletResponse response) {
        SysUser user = userService.getOne(new QueryWrapper<SysUser>((SysUser) new SysUser()
                .setUsername(username)
                .setIsDelete(false)));
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "用户不存在，请检查", null);
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "用户邮箱配置不正确，请联系系统管理员", null);
        }
        return getEmailCode(user.getEmail(), response);
    }

    /**
     * 获取邮箱验证码
     * @param email 接收验证码的邮箱
     * @return
     */
    @GetMapping("/email")
    @ResponseBody
    @OperationLog(name = "获取邮箱验证码", type = OperationLogType.OTHER)
    @ApiOperation(value = "获取邮箱验证码", response = ApiResult.class)
    public ApiResult<Map<String, Object>> getEmailCode(@RequestParam("email") String email, HttpServletResponse response) {
        // 邮箱不符合
        if (StringUtils.isEmpty(verificationCodeEmailSuffix)
                || !email.trim().endsWith(verificationCodeEmailSuffix)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "邮箱不符合规则，请使用公司邮箱" + verificationCodeEmailSuffix + "接收验证码", null);
        }

        try {
            VerificationCode verificationCode = new VerificationCode();
            String code = verificationCode.getCodeOnly(6, true);
            // 生成当前验证码会话token
            String verifyToken = UUIDUtil.getUuid();
            Map<String, Object> map = new HashMap<>(2);
            map.put(CommonConstant.VERIFY_TOKEN, verifyToken);

            // 发送验证码到邮箱
            String msg = "您本次的验证码为：" + code + "，验证码有效期 " + verificationCodeTimeout + " 分钟";
            emailService.sendSimpleMessage(email, "验证码", msg);

            // 缓存到Redis，key = 前缀 + token + 接收的邮箱
            stringRedisTemplate.opsForValue().set(TokenUtil.getRedisKey(verifyToken, email), code, verificationCodeTimeout, TimeUnit.MINUTES);

            return ApiResult.ok(map);
        } catch (Exception e) {
            log.error("发送邮件验证码错误", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ApiResult.result(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "服务器处理错误，请稍后重试", null);
        }
    }

    @PostMapping("/email")
    @ResponseBody
    @OperationLog(name = "校验邮箱验证码", type = OperationLogType.OTHER)
    @ApiOperation(value = "校验邮箱验证码", response = ApiResult.class)
    public ApiResult<String> checkEmailCode(@RequestBody VerificationCodeParam verificationCodeParam, HttpServletResponse response) {

        if (StringUtils.isBlank(verificationCodeParam.getVerifyToken())
                || StringUtils.isBlank(verificationCodeParam.getCode())
                || StringUtils.isBlank(verificationCodeParam.getReceiveClient())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "验证码校验失败",
                    "缺少必要参数:verifyToken,code,receiveClient");
        }

        try {
            verificationCodeService.checkVerifyCode(verificationCodeParam.getVerifyToken(), verificationCodeParam.getCode(), verificationCodeParam.getReceiveClient());
        } catch (VerificationCodeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.fail(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
        return ApiResult.ok(null, "验证码校验成功");
    }
}
