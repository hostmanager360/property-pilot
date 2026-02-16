package com.propertypilot.registration_service.service;

import com.propertypilot.registration_service.exception.EmailSendException;
import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.MailException;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

@Slf4j
@Service
public class SendMailServiceImpl implements SenEmailService {

    @Autowired
    private TemplateEngine templateEngine;

    private final JavaMailSender mailSender;

    public SendMailServiceImpl(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    @Override
    public void sendVerificationEmail(String to, String name, String link) {
        try {
            Context context = new Context(Locale.ITALIAN);
            context.setVariable("name", name);
            context.setVariable("verificationLink", link);

            String html = templateEngine.process("verification-email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(to);
            helper.setSubject("Verifica il tuo account");
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("Email verifica inviata a {}", to);

        } catch (MailException | MessagingException e) {
            log.error("Errore invio email verifica a {}: {}", to, e.getMessage(), e);
            throw new EmailSendException("Errore durante l'invio dell'email");
        }
    }

    @Override
    public void sendResetPasswordEmail(String to, String name, String link) {
        try {
            Context context = new Context(Locale.ITALIAN);
            context.setVariable("name", name);
            context.setVariable("resetLink", link);

            String html = templateEngine.process("reset-password-email", context);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(to);
            helper.setSubject("Reimposta la tua password");
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("Email reset password inviata a {}", to);

        } catch (MailException | MessagingException e) {
            log.error("Errore invio email reset password a {}: {}", to, e.getMessage(), e);
            throw new EmailSendException("Errore durante l'invio dell'email di reset password");
        }
    }

    @Override
    public void sendVerificationEmailWithInitialPassword(
            String to,
            String name,
            String email,
            String initialPassword,
            String verificationLink
    ) {
        try {
            Context ctx = new Context(Locale.ITALIAN);
            ctx.setVariable("name", name);
            ctx.setVariable("email", email);
            ctx.setVariable("initialPassword", initialPassword);
            ctx.setVariable("verificationLink", verificationLink);

            String html = templateEngine.process("verification-email", ctx);

            MimeMessage mimeMessage = mailSender.createMimeMessage();
            MimeMessageHelper helper = new MimeMessageHelper(
                    mimeMessage,
                    true,
                    StandardCharsets.UTF_8.name()
            );

            helper.setTo(to);
            helper.setSubject("Attiva il tuo account - PropertyPilot");
            helper.setText(html, true);

            mailSender.send(mimeMessage);
            log.info("Email attivazione (con password iniziale) inviata a {}", to);

        } catch (MailException | MessagingException e) {
            log.error("Errore invio email attivazione (con password) a {}: {}", to, e.getMessage(), e);
            throw new EmailSendException("Errore durante l'invio dell'email di attivazione");
        }
    }
}
