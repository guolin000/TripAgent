package com.yupi.yuaiagent.tools;

import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.tool.annotation.Tool;
import org.springframework.ai.tool.annotation.ToolParam;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.util.Properties;

/**
 * QQ邮箱邮件发送工具类
 */
@Slf4j
public class QQEmailSenderTool {

    // ⚠️ 请替换为你的QQ邮箱
    private static final String FROM_EMAIL = "347413502@qq.com";

    // ⚠️ 请替换为你的SMTP授权码（16位字符）
    private static final String AUTH_CODE = "tmlplyszibvybhec";

    // QQ邮箱SMTP配置
    private static final String SMTP_HOST = "smtp.qq.com";
    private static final String SMTP_PORT = "587"; // 推荐使用STARTTLS（端口587），也可用SSL（端口465）

    /**
     * 发送纯文本邮件
     *
     * @param to      收件人邮箱地址
     * @param subject 邮件主题
     * @param content 邮件正文（纯文本）
     * @throws MessagingException 发送失败时抛出异常
     */
    @Tool(description = "Sends an HTML formatted email using QQ Mail's SMTP service. " +
            "Requires the recipient's email address, subject line, and HTML content. " +
            "The sender's QQ email address and SMTP authorization code must be pre-configured in the system.")
    public static void sendTextEmail(
            @ToolParam(description = "Recipient's email address") String to,
            @ToolParam(description = "Email subject line") String subject,
            @ToolParam(description = "HTML content of the email") String content) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true"); // 启用STARTTLS加密（推荐）

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, AUTH_CODE);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setText(content);

        Transport.send(message);
        log.info("✅ 文本邮件发送成功！收件人：{}", to);
    }

    /**
     * 发送HTML格式邮件
     *
     * @param to           收件人邮箱
     * @param subject      邮件主题
     * @param htmlContent  HTML内容（支持标签如 <b>, <p>, <a> 等）
     * @throws MessagingException 发送失败时抛出
     */
    public static void sendHtmlEmail(
            String to,
            String subject,
            String htmlContent) throws MessagingException {

        Properties props = new Properties();
        props.put("mail.smtp.host", SMTP_HOST);
        props.put("mail.smtp.port", SMTP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(FROM_EMAIL, AUTH_CODE);
            }
        });

        Message message = new MimeMessage(session);
        message.setFrom(new InternetAddress(FROM_EMAIL));
        message.setRecipient(Message.RecipientType.TO, new InternetAddress(to));
        message.setSubject(subject);
        message.setContent(htmlContent, "text/html;charset=UTF-8");

        Transport.send(message);
        log.info("✅ HTML邮件发送成功！收件人：{}", to);
    }
}

