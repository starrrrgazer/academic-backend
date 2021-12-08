package com.example.portal.service;

import com.sun.tools.javac.util.Context;
import org.apache.logging.log4j.message.SimpleMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.RequestMapping;


public class MailServiceImpl implements MailService {
    @Autowired
    private JavaMailSender mailSender;

    @Value("${mail.mail.username}")
    private String from;

    @Override
    public void sendMail(String to, String subject, String verifyCode) {
        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setFrom(from);
        msg.setTo(to);
        msg.setSubject(subject);
        msg.setText("尊敬的用户，您好！\n"
                    + "\n您本次注册的验证码为 " + verifyCode + " ，本验证码5分钟内有效，请及时输入。\n"
                    + "（请勿泄露此验证码）\n\n如非本人操作，请忽略该邮件。\n(这是一封通过自动发送的邮件，请不要直接回复）\n");
        mailSender.send(msg);
    }
}
