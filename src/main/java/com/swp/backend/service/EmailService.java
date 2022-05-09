package com.swp.backend.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.FileSystemResource;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;
import java.io.File;
import java.util.Date;

@Service
public class EmailService {
    @Value("${spring.mail.username}")
    private String senderEmail;
    JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendSimpleMessage(String to, String subject, String text) {
        SimpleMailMessage message = new SimpleMailMessage();
        message.setFrom(senderEmail);
        message.setTo(to);
        message.setSubject(subject);
        message.setText(text);
        new Thread(() -> {
            System.out.println("Thread " + Thread.currentThread().getId() + " start send text email at: " + new Date(System.currentTimeMillis()));
            mailSender.send(message);
            System.out.println("Thread " + Thread.currentThread().getId() + " finished send text email at: " + new Date(System.currentTimeMillis()));
        }).start();
    }

    public void sendMessageWithAttachment(String to, String subject, String text, String pathToAttachment) throws MessagingException {
        MimeMessage message = mailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true);

        helper.setFrom(senderEmail);
        helper.setTo(to);
        helper.setSubject(subject);
        helper.setText(text);

        File file = new File(pathToAttachment);
        helper.addAttachment(file.getName(), file);
        new Thread(() -> {
            System.out.println("Thread " + Thread.currentThread().getId() + " start send attachment email at: " + new Date(System.currentTimeMillis()));
            mailSender.send(message);
            System.out.println("Thread " + Thread.currentThread().getId() + " finished send attachment email at: " + new Date(System.currentTimeMillis()));
        }).start();
    }
}
