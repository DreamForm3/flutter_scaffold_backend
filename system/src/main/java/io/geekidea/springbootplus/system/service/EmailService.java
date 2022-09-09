package io.geekidea.springbootplus.system.service;

/**
 * 邮件服务
 */
public interface EmailService {

    /**
     * 发送简单邮件
     * @param to 收件人
     * @param subject 主题
     * @param text 邮件正文
     */
    void sendSimpleMessage(String to, String subject, String text);
}
