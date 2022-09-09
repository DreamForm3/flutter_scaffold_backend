package io.geekidea.springbootplus.system.service.impl;

import io.geekidea.springbootplus.framework.util.TokenUtil;
import io.geekidea.springbootplus.system.exception.VerificationCodeException;
import io.geekidea.springbootplus.system.service.VerificationCodeService;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Lazy;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

@Component
public class VerificationCodeServiceImpl implements VerificationCodeService {

    @Lazy
    @Autowired
    private StringRedisTemplate stringRedisTemplate;

    @Override
    public void checkVerifyCode(String verifyToken, String code) throws VerificationCodeException {
        checkVerifyCode(verifyToken, code, null);
    }

    @Override
    public void checkVerifyCode(String verifyToken, String code, String receiveClient) throws VerificationCodeException {

        // 校验验证码
        if (StringUtils.isBlank(code)) {
            throw new VerificationCodeException("请输入验证码");
        }
        // 从redis中获取
        String redisKey = TokenUtil.getRedisKey(verifyToken, receiveClient);
        String generateCode = (String) stringRedisTemplate.opsForValue().get(redisKey);
        if (StringUtils.isBlank(generateCode)) {
            throw new VerificationCodeException("验证码已过期或不正确");
        }
        // 不区分大小写
        if (!generateCode.equalsIgnoreCase(code)) {
            throw new VerificationCodeException("验证码错误");
        }
        // 验证码校验成功，删除Redis缓存
        stringRedisTemplate.delete(redisKey);
    }
}
