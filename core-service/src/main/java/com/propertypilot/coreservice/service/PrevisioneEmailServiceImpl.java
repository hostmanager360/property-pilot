package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.config.CurrentUserProvider;
import com.propertypilot.coreservice.dto.PdfResult;
import com.propertypilot.coreservice.exceptionCustom.ErrorCode;
import com.propertypilot.coreservice.exceptionCustom.PrevisioneGuadagnoException;
import com.propertypilot.coreservice.model.PrevisioneGuadagno;
import com.propertypilot.coreservice.model.User;
import com.propertypilot.coreservice.repository.PrevisioneGadagnoRepository;
import com.propertypilot.coreservice.util.EmailSender;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Locale;
import java.util.regex.Pattern;

@Slf4j
@Service
@RequiredArgsConstructor
public class PrevisioneEmailServiceImpl implements PrevisioneEmailService {

    @Autowired
    PrevisioneGadagnoRepository previsioneGadagnoRepository;
    @Autowired
    CurrentUserProvider currentUserProvider;
    @Autowired
    PdfService pdfService;
    @Autowired
    EmailSender emailSender;

    @Value("${propertypilot.mail.from}")
    private String mailFrom;

    private static final String TEMPLATE_CLASSPATH = "templates/previsione-email.html";
    private static final String LOGO_CLASSPATH = "img/logo.jpg";

    private static final Pattern SIMPLE_EMAIL =
            Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,}$", Pattern.CASE_INSENSITIVE);

    @Override
    @Transactional(readOnly = true)
    public void sendPrevisioneToOwner(Integer previsioneId, String ownerEmail, String ownerName) {

        User currentUser = currentUserProvider.getCurrentUserOrThrow();

        if (previsioneId == null) {
            throw new PrevisioneGuadagnoException(ErrorCode.VALIDATION_ERROR, "previsioneId obbligatorio");
        }

        String to = ownerEmail != null ? ownerEmail.trim() : "";
        if (to.isBlank() || !SIMPLE_EMAIL.matcher(to).matches()) {
            throw new PrevisioneGuadagnoException(ErrorCode.PREVISIONE_EMAIL_INVALID, "Email proprietario non valida");
        }

        PrevisioneGuadagno p = previsioneGadagnoRepository.findById(previsioneId)
                .orElseThrow(() -> new PrevisioneGuadagnoException(
                        ErrorCode.PREVISIONE_NOT_FOUND, "Previsione non trovata"
                ));

        // ✅ ownership check (coerente con quello che già usi)
        if (p.getUser() == null || p.getUser().getId() == null ||
                !p.getUser().getId().equals(currentUser.getId())) {
            log.warn("Invio email negato: previsioneId={}, ownerUserId={}, currentUserId={}",
                    previsioneId,
                    p.getUser() != null ? p.getUser().getId() : null,
                    currentUser.getId());
            throw new PrevisioneGuadagnoException(ErrorCode.PREVISIONE_FORBIDDEN, "Accesso non consentito alla previsione");
        }

        // ✅ genera PDF (riuso logica già funzionante)
        PdfResult pdf = pdfService.generatePrevisionePdf(previsioneId);

        byte[] pdfBytes = extractPdfBytes(pdf);
        String pdfFilename = extractPdfFilename(pdf);

        if (pdfBytes == null || pdfBytes.length == 0) {
            throw new PrevisioneGuadagnoException(ErrorCode.GENERIC_ERROR, "PDF vuoto/non generato");
        }

        String aptName = safeText(nvl(p.getNomeAppartamento()), "Appartamento");
        String subject = "Previsione guadagno - " + aptName;

        // HTML da file + placeholder replace
        String template = loadTemplate(TEMPLATE_CLASSPATH);
        String html = render(template, p.getNomeAppartamento(), p.getIndirizzo(), ownerName);

        try {
            emailSender.sendHtmlWithPdfAttachment(
                    to.toLowerCase(Locale.ROOT),
                    subject,
                    html,
                    mailFrom,
                    pdfFilename,
                    pdfBytes,
                    LOGO_CLASSPATH

            );

            log.info("Email inviata con PDF: previsioneId={}, fromUserId={}, to={}, file={}",
                    previsioneId, currentUser.getId(), to, pdfFilename);

        } catch (Exception e) {
            log.error("Errore invio email: previsioneId={}, to={}, msg={}",
                    previsioneId, to, e.getMessage(), e);

            throw new PrevisioneGuadagnoException(
                    ErrorCode.PREVISIONE_EMAIL_SEND_FAILED,
                    "Invio email non riuscito"
            );
        }
    }

    // =========================
    // Template loader + merge
    // =========================

    public String loadTemplate(String classpath) {
        try {
            ClassPathResource res = new ClassPathResource(classpath);
            if (!res.exists()) {
                throw new IllegalStateException("Template email non trovato: " + classpath);
            }
            byte[] bytes = res.getInputStream().readAllBytes();
            return new String(bytes, StandardCharsets.UTF_8);
        } catch (Exception e) {
            log.error("Errore lettura template email {}: {}", classpath, e.getMessage(), e);
            throw new IllegalStateException("Impossibile caricare template email", e);
        }
    }

    public String render(String template, String apartmentName, String apartmentAddress, String ownerName) {
        String name = safe(apartmentName);
        String address = safe(apartmentAddress);
        String owner = safe(ownerName);

        if (name.isBlank()) name = "Appartamento";
        if (address.isBlank()) address = "Indirizzo non specificato";
        if (owner.isBlank()) owner = "Gentile Proprietario/a";

        return template
                .replace("{{APARTMENT_NAME}}", name)
                .replace("{{APARTMENT_ADDRESS}}", address)
                .replace("{{OWNER_NAME}}", owner);
    }

    private String safe(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String escapeHtml(String s) {
        if (s == null) return "";
        return s.replace("&", "&amp;").replace("<", "&lt;").replace(">", "&gt;");
    }

    private String nvl(String s) { return s == null ? "" : s; }

    private String safeText(String s, String fallback) {
        String x = s == null ? "" : s.trim();
        return x.isBlank() ? fallback : x;
    }

    // =========================
    // PdfResult compat layer
    // (adatta se PdfResult non è record)
    // =========================

    private byte[] extractPdfBytes(PdfResult pdf) {
        // Se PdfResult è un record: pdf.bytes()
        // Se è una classe: pdf.getBytes()
        try {
            return (byte[]) PdfResult.class.getMethod("bytes").invoke(pdf);
        } catch (Exception ignored) {
        }
        try {
            return (byte[]) PdfResult.class.getMethod("getBytes").invoke(pdf);
        } catch (Exception ignored) {
        }
        return null;
    }

    private String extractPdfFilename(PdfResult pdf) {
        try {
            Object v = PdfResult.class.getMethod("filename").invoke(pdf);
            return v != null ? v.toString() : "previsione.pdf";
        } catch (Exception ignored) {
        }
        try {
            Object v = PdfResult.class.getMethod("getFilename").invoke(pdf);
            return v != null ? v.toString() : "previsione.pdf";
        } catch (Exception ignored) {
        }
        return "previsione.pdf";
    }
}