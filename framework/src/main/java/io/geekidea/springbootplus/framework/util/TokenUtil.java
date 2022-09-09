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

package io.geekidea.springbootplus.framework.util;

import io.geekidea.springbootplus.config.constant.CommonRedisKey;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.lang3.StringUtils;

import java.util.Base64;

/**
 * @author geekidea
 * @date 2018-11-08
 */
public class TokenUtil {

    /**
     *
     * @return
     */
    public static String generateFirstLoginRestPwdToken(){
        String token = "first-login-rest-pwd-token:" + UUIDUtil.getUuid();
        return token;
    }

    /**
     * 生成验证码token
     * @return
     */
    public static String generateVerificationCodeToken(){
        String token = "verification-code-token:" + UUIDUtil.getUuid();
        return token;
    }

    /**
     * 获取验证码的 redis key
     * @param verifyToken
     * @param receiveClient
     * @return
     */
    public static String getRedisKey(String verifyToken, String receiveClient) {
        if (StringUtils.isBlank(verifyToken)) {
            throw new NullPointerException("verifyToken 不能为空");
        }
        // redis的key如果有接收的邮箱或者手机号，则key = 前缀 + token + 接收的邮箱或者手机号，否则key = 前缀 + token
        // 邮箱或者手机号要用sha256加密处理
        if (StringUtils.isNotBlank(receiveClient)) {
            verifyToken = verifyToken + DigestUtils.sha256Hex(receiveClient);
        }
        String redisKey = String.format(CommonRedisKey.VERIFY_CODE, verifyToken);
        return redisKey;
    }
}
