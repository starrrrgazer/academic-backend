package com.example.portal.service;

public interface MailService {
    public void sendMail(String to, String subject, String verifyCode);
}