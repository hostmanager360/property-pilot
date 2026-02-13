package com.propertypilot.coreservice.util;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailSender {

    @Autowired
    JavaMailSender mailSender;
    public void sendHtmlWithPdfAttachment(
            String to,
            String subject,
            String html,
            String from,
            String attachmentFilename,
            byte[] attachmentBytes,
            String logoClasspath // es: "img/logo.jpg"
    ) throws MessagingException {

        MimeMessage message = mailSender.createMimeMessage();

        // multipart = true per allegati
        MimeMessageHelper helper = new MimeMessageHelper(
                message,
                true,
                StandardCharsets.UTF_8.name()
        );

        helper.setTo(to);
        helper.setSubject(subject);
        helper.setFrom(from);
        helper.setText(html, true);

        // Allegato PDF
        helper.addAttachment(
                attachmentFilename,
                () -> new java.io.ByteArrayInputStream(attachmentBytes),
                "application/pdf"
        );
        // Inline logo (cid:logo)
        try {
            ClassPathResource logo = new ClassPathResource(logoClasspath);
            if (logo.exists()) {
                helper.addInline("logo", logo); // id deve combaciare con cid:logo
            } else {
                log.warn("Logo non trovato per inline: {}", logoClasspath);
            }
        } catch (Exception e) {
            log.warn("Impossibile aggiungere logo inline {}: {}", logoClasspath, e.getMessage());
        }
        mailSender.send(message);
        log.info("Email inviata: to={}, subject={}", to, subject);
    }
}
