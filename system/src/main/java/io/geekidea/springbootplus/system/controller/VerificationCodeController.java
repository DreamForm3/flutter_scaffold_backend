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
 * ???????????????
 *
 * @author geekidea
 * @date 2019-10-27
 **/
@Slf4j
@Controller
@Api(value = "?????????API", tags = {"?????????"})
@Module("system")
@RequestMapping("/verificationCode")
/*@ConditionalOnProperty(value = {"spring-boot-plus.enable-verify-code"}, matchIfMissing = true)*/
public class VerificationCodeController {

    /**
     * ??????????????? 5 ????????????
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
     * ???????????????
     */
    @GetMapping("/getImage")
    @OperationLog(name = "???????????????", type = OperationLogType.OTHER)
    @ApiOperation(value = "???????????????", response = ApiResult.class)
    public void getImage(HttpServletResponse response) throws Exception {
        VerificationCode verificationCode = new VerificationCode();
        BufferedImage image = verificationCode.getImage();
        String code = verificationCode.getText();
        String verifyToken = UUIDUtil.getUuid();
        // ?????????Redis
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
     * ????????????Base64?????????
     */
    @GetMapping("/getBase64Image")
    @ResponseBody
    @OperationLog(name = "????????????Base64?????????", type = OperationLogType.OTHER)
    @ApiOperation(value = "????????????Base64?????????", response = ApiResult.class)
    public ApiResult<Map<String, Object>> getCode(HttpServletResponse response) throws Exception {
        VerificationCode verificationCode = new VerificationCode();
        BufferedImage image = verificationCode.getImage();
        String code = verificationCode.getText();
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        ImageIO.write(image, CommonConstant.JPEG, outputStream);
        // ??????????????????base64?????????
        String base64 = Base64.getEncoder().encodeToString(outputStream.toByteArray());
        // ???????????????????????????token
        String verifyToken = UUIDUtil.getUuid();
        Map<String, Object> map = new HashMap<>(2);
        map.put(CommonConstant.IMAGE, CommonConstant.BASE64_PREFIX + base64);
        map.put(CommonConstant.VERIFY_TOKEN, verifyToken);
        // ?????????Redis
        stringRedisTemplate.opsForValue().set(TokenUtil.getRedisKey(verifyToken, null), code, verificationCodeTimeout, TimeUnit.MINUTES);
        return ApiResult.ok(map);
    }

    /**
     * ????????????????????????????????????
     * @param username ???????????????????????????
     * @return
     */
    @GetMapping("/username")
    @ResponseBody
    @OperationLog(name = "????????????????????????????????????", type = OperationLogType.OTHER)
    @ApiOperation(value = "????????????????????????????????????", response = ApiResult.class)
    public ApiResult<Map<String, Object>> getEmailCodeByUsername(@RequestParam("username") String username, HttpServletResponse response) {
        SysUser user = userService.getOne(new QueryWrapper<SysUser>((SysUser) new SysUser()
                .setUsername(username)
                .setIsDelete(false)));
        if (user == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "???????????????????????????", null);
        }
        if (StringUtils.isEmpty(user.getEmail())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "??????????????????????????????????????????????????????", null);
        }
        return getEmailCode(user.getEmail(), response);
    }

    /**
     * ?????????????????????
     * @param email ????????????????????????
     * @return
     */
    @GetMapping("/email")
    @ResponseBody
    @OperationLog(name = "?????????????????????", type = OperationLogType.OTHER)
    @ApiOperation(value = "?????????????????????", response = ApiResult.class)
    public ApiResult<Map<String, Object>> getEmailCode(@RequestParam("email") String email, HttpServletResponse response) {
        // ???????????????
        if (StringUtils.isEmpty(verificationCodeEmailSuffix)
                || !email.trim().endsWith(verificationCodeEmailSuffix)) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "?????????????????????????????????????????????" + verificationCodeEmailSuffix + "???????????????", null);
        }

        try {
            VerificationCode verificationCode = new VerificationCode();
            String code = verificationCode.getCodeOnly(6, true);
            // ???????????????????????????token
            String verifyToken = UUIDUtil.getUuid();
            Map<String, Object> map = new HashMap<>(2);
            map.put(CommonConstant.VERIFY_TOKEN, verifyToken);

            // ????????????????????????
            String msg = "???????????????????????????" + code + "????????????????????? " + verificationCodeTimeout + " ??????";
            emailService.sendSimpleMessage(email, "?????????", msg);

            // ?????????Redis???key = ?????? + token + ???????????????
            stringRedisTemplate.opsForValue().set(TokenUtil.getRedisKey(verifyToken, email), code, verificationCodeTimeout, TimeUnit.MINUTES);

            return ApiResult.ok(map);
        } catch (Exception e) {
            log.error("???????????????????????????", e);
            response.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            return ApiResult.result(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "???????????????????????????????????????", null);
        }
    }

    @PostMapping("/email")
    @ResponseBody
    @OperationLog(name = "?????????????????????", type = OperationLogType.OTHER)
    @ApiOperation(value = "?????????????????????", response = ApiResult.class)
    public ApiResult<String> checkEmailCode(@RequestBody VerificationCodeParam verificationCodeParam, HttpServletResponse response) {

        if (StringUtils.isBlank(verificationCodeParam.getVerifyToken())
                || StringUtils.isBlank(verificationCodeParam.getCode())
                || StringUtils.isBlank(verificationCodeParam.getReceiveClient())) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.result(HttpServletResponse.SC_BAD_REQUEST, "?????????????????????",
                    "??????????????????:verifyToken,code,receiveClient");
        }

        try {
            verificationCodeService.checkVerifyCode(verificationCodeParam.getVerifyToken(), verificationCodeParam.getCode(), verificationCodeParam.getReceiveClient());
        } catch (VerificationCodeException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            return ApiResult.fail(HttpServletResponse.SC_BAD_REQUEST, e.getMessage());
        }
        return ApiResult.ok(null, "?????????????????????");
    }
}
