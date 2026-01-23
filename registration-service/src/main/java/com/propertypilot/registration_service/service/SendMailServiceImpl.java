package com.propertypilot.registration_service.service;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;

import org.thymeleaf.context.Context;

@Slf4j
@Service
public class SendMailServiceImpl implements SenEmailService{

    @Autowired
    private TemplateEngine templateEngine;

    private final JavaMailSender mailSender;

    public SendMailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    public void sendVerificationEmail(String to,String name, String link) {
        SimpleMailMessage message = new SimpleMailMessage();
        try {
            Context context = new Context();
            context.setVariable("name", name);
            context.setVariable("verificationLink", link);

            String html = templateEngine.process("verification-email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(mimeMessage, true);

            helper.setTo(to);
            helper.setSubject("Verifica il tuo account");
            helper.setText(html, true);

            mailSender.send(mimeMessage);

        } catch (MailException e) {
            log.error("Errore durante l'invio email: {}", e.getMessage());
            throw new RuntimeException("Impossibile inviare email");
        } catch (MessagingException e) {
            throw new RuntimeException(e);
        }


    }

}
