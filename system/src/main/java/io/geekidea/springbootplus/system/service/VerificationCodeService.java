package io.geekidea.springbootplus.system.service;

import io.geekidea.springbootplus.system.exception.VerificationCodeException;

/**
 * 验证码相关
 */
public interface VerificationCodeService {

    /**
     * 校验验证码是否正确
     * @param verifyToken token
     * @param code 验证码
     * @throws Exception
     */
    void checkVerifyCode(String verifyToken, String code) throws VerificationCodeException;

    /**
     * 校验验证码是否正确
     * @param verifyToken token
     * @param code 验证码
     * @param receiveClient 接收验证码的邮箱或者手机号码
     * @throws Exception
     */
    void checkVerifyCode(String verifyToken, String code, String receiveClient) throws VerificationCodeException;
}
