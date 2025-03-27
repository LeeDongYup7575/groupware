package com.example.projectdemo.domain.auth.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender emailSender;

    @Autowired
    private TemplateEngine templateEngine;

    public void sendTempPasswordEmail(String to, String tempPassword) {
        try {
            // MimeMessage 생성
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            // 이메일 기본 정보 설정
            helper.setFrom("your-company-email@example.com");
            helper.setTo(to);
            helper.setSubject("임시 비밀번호 안내");

            // Thymeleaf 템플릿 사용
            Context context = new Context();
            context.setVariable("tempPassword", tempPassword);
            String htmlContent = templateEngine.process("auth/temp-password", context);

            // HTML 콘텐츠 설정
            helper.setText(htmlContent, true);

            // 이메일 전송
            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 전송 중 오류가 발생했습니다.", e);
        }
    }

    public void sendVerificationEmail(String to, String name, String verificationLink) {
        try {
            MimeMessage message = emailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");

            helper.setTo(to);
            helper.setSubject("그룹웨어 이메일 인증 안내");

            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("verificationLink", verificationLink);

            String htmlContent = templateEngine.process("auth/email-verification", context);
            helper.setText(htmlContent, true);

            emailSender.send(message);
        } catch (MessagingException e) {
            throw new RuntimeException("이메일 인증 메일 발송 중 오류가 발생했습니다.", e);
        }
    }
}