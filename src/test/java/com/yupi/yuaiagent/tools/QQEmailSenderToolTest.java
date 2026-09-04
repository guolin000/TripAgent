package com.yupi.yuaiagent.tools;

import org.junit.jupiter.api.Test;

import javax.mail.MessagingException;

public class QQEmailSenderToolTest {
    @Test
    void sendTextEmail() {
        try {
            QQEmailSenderTool.sendTextEmail(
                    "sgl17600726166@163.com",  // 替换为目标邮箱
                    "测试邮件",
                    "这是一封通过Java发送的测试邮件。"
            );
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }

    @Test
    void sendHtmlEmail() {
        try {
            String html = "<h2>🎉 这是一封HTML测试邮件</h2>" +
                    "<p><b>加粗内容</b>，<a href='https://example.com'>点击链接</a></p>";
            QQEmailSenderTool.sendHtmlEmail("recipient@example.com", "HTML测试", html);
        } catch (MessagingException e) {
            e.printStackTrace();
        }
    }
}
