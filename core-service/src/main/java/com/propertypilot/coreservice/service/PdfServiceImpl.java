package com.propertypilot.coreservice.service;

import com.lowagie.text.*;
import com.lowagie.text.pdf.*;
import com.propertypilot.coreservice.config.CurrentUserProvider;
import com.propertypilot.coreservice.dto.PdfResult;
import com.propertypilot.coreservice.exceptionCustom.ErrorCode;
import com.propertypilot.coreservice.exceptionCustom.PrevisioneGuadagnoException;
import com.propertypilot.coreservice.model.PrevisioneGuadagno;
import com.propertypilot.coreservice.model.User;
import com.propertypilot.coreservice.repository.PrevisioneGadagnoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ClassPathResource;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.awt.Color;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.Normalizer;
import java.util.Locale;

@Slf4j
@Service
@RequiredArgsConstructor
public class PdfServiceImpl implements PdfService {

    private final PrevisioneGadagnoRepository previsioneGadagnoRepository;
    private final CurrentUserProvider currentUserProvider;

    // =========================
    // Testi “fissi” (come richiesto)
    // =========================
    private static final String DISCLAIMER_TEXT =
            "Questa analisi previsionale offre una stima basata sull’andamento attuale del mercato e sui dati medi di occupazione disponibili. "
                    + "Si tratta di una proiezione realistica di potenziali ricavi e costi, elaborata considerando il comportamento dei competitor, "
                    + "i flussi turistici e l’andamento di periodi analoghi. "
                    + "Non è un risultato garantito, ma un obiettivo raggiungibile con una gestione professionale, costante e con un pricing dinamico adeguato.";

    private static final String FOOTER_NOTE_TEXT =
            "Con una strategia iniziale efficace, una gestione puntuale e un monitoraggio continuo del pricing, questi risultati sono del tutto realistici. "
                    + "Una gestione professionale permette di ottimizzare i profitti e garantire un ritorno economico stabile e sostenibile per la proprietaria.";

    // Logo in resources
    private static final String LOGO_CLASSPATH = "img/logo.jpg";

    @Override
    @Transactional(readOnly = true)
    public PdfResult generatePrevisionePdf(Integer id) {

        User currentUser = currentUserProvider.getCurrentUserOrThrow();

        PrevisioneGuadagno p = previsioneGadagnoRepository.findById(id)
                .orElseThrow(() -> new PrevisioneGuadagnoException(
                        ErrorCode.PREVISIONE_NOT_FOUND,
                        "Previsione non trovata: id=" + id
                ));

        // ownership check
        if (p.getUser() == null || p.getUser().getId() == null ||
                !p.getUser().getId().equals(currentUser.getId())) {

            log.warn("Accesso negato al PDF: previsioneId={}, ownerUserId={}, currentUserId={}",
                    id,
                    p.getUser() != null ? p.getUser().getId() : null,
                    currentUser.getId());

            throw new PrevisioneGuadagnoException(
                    ErrorCode.PREVISIONE_FORBIDDEN,
                    "Accesso non consentito alla previsione id=" + id
            );
        }

        final byte[] bytes;
        try {
            bytes = buildPdf(p);
        } catch (PrevisioneGuadagnoException e) {
            throw e; // se buildPdf lancia già custom, non mascherare
        } catch (Exception e) {
            log.error("Errore generazione PDF previsioneId={}", id, e);
            throw new PrevisioneGuadagnoException(
                    ErrorCode.PREVISIONE_PDF_ERROR,
                    "Errore durante la generazione del PDF"
            );
        }

        if (bytes == null || bytes.length == 0) {
            log.error("PDF generato vuoto: previsioneId={}, userId={}", id, currentUser.getId());
            throw new PrevisioneGuadagnoException(
                    ErrorCode.PREVISIONE_PDF_EMPTY,
                    "PDF generato vuoto per previsione id=" + id
            );
        }

        String baseName = safeFilename(nvl(p.getNomeAppartamento()));
        if (baseName.isBlank()) baseName = "appartamento";
        String filename = "previsione_" + baseName + ".pdf";

        log.info("PDF generato: previsioneId={}, userId={}, filename={}, size={} bytes",
                id, currentUser.getId(), filename, bytes.length);

        return new PdfResult(bytes, filename);
    }

    private byte[] buildPdf(PrevisioneGuadagno p) {
        // Palette
        final Color GOLD = new Color(212, 175, 55);
        final Color DARK = new Color(25, 25, 25);
        final Color MUTED = new Color(90, 90, 90);
        final Color LIGHT_BG = new Color(248, 248, 248);
        final Color BORDER = new Color(220, 220, 220);

        // Fonts (OpenPDF/lowagie)
        Font headerSmall = new Font(Font.HELVETICA, 13, Font.BOLD, DARK);   // PROPOSTA...
        Font headerName  = new Font(Font.HELVETICA, 16, Font.BOLD, DARK);   // nome apt
        Font headerAddr  = new Font(Font.HELVETICA, 10, Font.NORMAL, MUTED);// indirizzo
        Font monthFont   = new Font(Font.HELVETICA, 10, Font.BOLD, DARK);

        Font h2 = new Font(Font.HELVETICA, 12, Font.BOLD, DARK);
        Font normal = new Font(Font.HELVETICA, 10, Font.NORMAL, DARK);
        Font muted = new Font(Font.HELVETICA, 9, Font.NORMAL, MUTED);
        Font bold = new Font(Font.HELVETICA, 10, Font.BOLD, DARK);
        Font bigKpi = new Font(Font.HELVETICA, 14, Font.BOLD, DARK);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        Document doc = new Document(PageSize.A4, 42, 42, 40, 48);
        PdfWriter writer = null;

        try {
            writer = PdfWriter.getInstance(doc, baos);
            writer.setPageEvent(new PageFooter());
            doc.open();

            // ===== Barra oro con logo + titolo dentro =====
            addGoldHeaderBar(doc, GOLD, headerSmall);

            // ===== Sotto barra: solo nome + indirizzo =====
            addApartmentHeader(doc, p, headerName, headerAddr);

            // Disclaimer (ora costante + migliore, senza luogo)
            Paragraph disclaimer = new Paragraph(DISCLAIMER_TEXT, muted);
            disclaimer.setSpacingBefore(8f);
            disclaimer.setSpacingAfter(14f);
            disclaimer.setAlignment(Element.ALIGN_JUSTIFIED);
            doc.add(disclaimer);

            // ===== Ricavi Lordi =====
            sectionTitle(doc, "Ricavi Lordi", h2, GOLD);

            BigDecimal prezzoNotte = bd(p.getPrezzoMedioPerNotte());
            int notti = safeInt(p.getNottiMensili());
            BigDecimal lordoPern = bd(p.getTotaleLordoPernottamenti());

            doc.add(kvLine("Prezzo medio per notte:", eur(prezzoNotte), normal, bold));
            doc.add(kvLine("Occupazione media:", notti + " notti/mese", normal, bold));
            doc.add(new Paragraph(" "));

            PdfPTable calc = boxedTable(BORDER, LIGHT_BG);
            calc.addCell(boxLine("Ricavi da pernottamenti", bold));
            calc.addCell(boxLine("Calcolo: " + notti + " notti × " + eur(prezzoNotte) + " / notte", muted));
            calc.addCell(boxLine("Totale lordo pernottamenti: " + eur(lordoPern), bold));
            doc.add(calc);

            doc.add(new Paragraph(" "));

            // ===== Commissioni e Costi =====
            sectionTitle(doc, "Commissioni e Costi", h2, GOLD);

            BigDecimal percPiattaforma = bd(p.getCostoPiattaforma());          // %
            BigDecimal totPiattaforma = bd(p.getTotaleCostoPiattaforma());

            BigDecimal percTasse = bd(p.getCostoTasse());                      // %
            BigDecimal totTasse = bd(p.getTotaleCostoTassa());

            // ✅ regola tua: se costoTasse valorizzato (>0) allora includi, altrimenti no
            boolean tasseAttive = percTasse.compareTo(BigDecimal.ZERO) > 0;

            BigDecimal pulizia = bd(p.getCostoPulizia());
            int pren = safeInt(p.getNumeroPrenotazioni());
            BigDecimal totPulizia = bd(p.getTotaleCostoPulizia());

            BigDecimal utenzeMensili = bd(p.getCostoUtenzeMensili());
            BigDecimal mutuoAffitto = bd(p.getMutuoAffitto());

            PdfPTable costs = simpleTable(new float[]{60, 40});
            addRow(costs, "Commissione piattaforma (" + pct(percPiattaforma) + ")", eur(totPiattaforma), normal, bold);

            if (tasseAttive) {
                addRow(costs, "Tasse (" + pct(percTasse) + ")", eur(totTasse), normal, bold);
            }

            addRow(costs, "Pulizie (" + eur(pulizia) + " × " + pren + ")", eur(totPulizia), normal, bold);
            addRow(costs, "Utenze mensili", eur(utenzeMensili), normal, bold);
            addRow(costs, "Mutuo / Affitto", eur(mutuoAffitto), normal, bold);
            doc.add(costs);

            doc.add(new Paragraph(" "));

            // ===== Ripartizione ricavi / gestione (vista proprietario) =====
            sectionTitle(doc, "Ripartizione dei Ricavi", h2, GOLD);

            BigDecimal lordoGestione = bd(p.getTotaleLordoGestione());
            BigDecimal percGestioneTot = bd(p.getCommissioneGestioneTotale());

            PdfPTable split = simpleTable(new float[]{60, 40});

            // label unica con percentuale accanto
            String labelLordoGestione = percGestioneTot.compareTo(BigDecimal.ZERO) > 0
                    ? "Lordo gestione (" + pct(percGestioneTot) + ")"
                    : "Lordo gestione";

            addRow(split, labelLordoGestione, eur(lordoGestione), normal, bold);

            doc.add(split);

            doc.add(new Paragraph(" "));

            // ===== Netto proprietario =====
            sectionTitle(doc, "Risultato Finale Mensile Medio", h2, GOLD);

            BigDecimal netto = bd(p.getTotaleNettoProprietario());

            PdfPTable result = boxedTable(BORDER, new Color(250, 250, 250));
            result.addCell(boxLine("Netto al Proprietario", bold));
            result.addCell(boxLine("Ricavo netto mensile: " + eur(netto), bigKpi));
            result.addCell(boxLine(
                    "Include: piattaforma, " + (tasseAttive ? "tasse, " : "") + "pulizie, utenze, mutuo/affitto e gestione",
                    muted
            ));
            doc.add(result);

            // ✅ testo finale in fondo
            Paragraph footerNote = new Paragraph(FOOTER_NOTE_TEXT, muted);
            footerNote.setSpacingBefore(14f);
            footerNote.setAlignment(Element.ALIGN_JUSTIFIED);
            doc.add(footerNote);

            // IMPORTANTISSIMO: chiudi
            doc.close();
            writer.close();

            byte[] out = baos.toByteArray();
            if (out.length == 0) {
                throw new IllegalStateException("PDF costruito ma output vuoto");
            }
            return out;

        } catch (Exception e) {
            log.error("Errore generazione PDF previsioneId={}: {}", p.getId(), e.getMessage(), e);
            try { if (doc.isOpen()) doc.close(); } catch (Exception ignored) {}
            try { if (writer != null) writer.close(); } catch (Exception ignored) {}
            throw new IllegalStateException("Errore durante la generazione del PDF", e);
        }
    }

    /**
     * Header richiesto:
     * - logo (da classpath img/logo.jpg)
     * - PROPOSTA... centrato
     * - NOME appartamento centrato (più grande)
     * - indirizzo centrato più piccolo sotto
     */
    private void addCenteredHeaderWithLogo(
            Document doc,
            PrevisioneGuadagno p,
            Font headerSmall,
            Font headerName,
            Font headerAddr,
            Font monthFont
    ) throws Exception {

        final float[] widths = new float[]{18f, 64f, 18f}; // 3 colonne: logo | testi centrati | vuoto
        PdfPTable header = new PdfPTable(widths);
        header.setWidthPercentage(100);

        // --- col 1: logo ---
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setHorizontalAlignment(Element.ALIGN_LEFT);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Image logo = loadLogoOrNull();
        if (logo != null) {
            // dimensione “pulita” (aggiusta se vuoi)
            logo.scaleToFit(70f, 70f);
            logoCell.addElement(logo);
        } else {
            // se non c’è logo, lascia vuoto senza rompere il layout
            logoCell.addElement(new Phrase(" "));
        }
        header.addCell(logoCell);

        // --- col 2: testi centrati ---
        PdfPCell center = new PdfPCell();
        center.setBorder(Rectangle.NO_BORDER);
        center.setHorizontalAlignment(Element.ALIGN_CENTER);

        Paragraph p1 = new Paragraph("PROPOSTA GESTIONE ONLINE", headerSmall);
        p1.setAlignment(Element.ALIGN_CENTER);
        p1.setSpacingAfter(2f);

        Paragraph p2 = new Paragraph(nvl(p.getNomeAppartamento()).toUpperCase(Locale.ITALIAN), headerName);
        p2.setAlignment(Element.ALIGN_CENTER);
        p2.setSpacingAfter(2f);

        Paragraph p3 = new Paragraph(nvl(p.getIndirizzo()), headerAddr);
        p3.setAlignment(Element.ALIGN_CENTER);

        /*String month = LocalDate.now().getMonth()
                .getDisplayName(TextStyle.FULL, Locale.ITALIAN)
                .toUpperCase(Locale.ITALIAN);

        Paragraph p4 = new Paragraph(month, monthFont);
        p4.setAlignment(Element.ALIGN_CENTER);
        p4.setSpacingBefore(6f);*/

        center.addElement(p1);
        center.addElement(p2);
        if (!nvl(p.getIndirizzo()).isBlank()) center.addElement(p3);
       // center.addElement(p4);

        header.addCell(center);

        // --- col 3: vuoto (bilancia per mantenere il centro davvero centrato) ---
        PdfPCell right = new PdfPCell(new Phrase(" "));
        right.setBorder(Rectangle.NO_BORDER);
        header.addCell(right);

        doc.add(header);
        doc.add(new Paragraph(" "));
    }

    private Image loadLogoOrNull() {
        try {
            ClassPathResource res = new ClassPathResource(LOGO_CLASSPATH);
            if (!res.exists()) {
                log.warn("Logo non trovato a classpath:{} (mettilo in src/main/resources/{})", LOGO_CLASSPATH, LOGO_CLASSPATH);
                return null;
            }
            try (InputStream is = res.getInputStream()) {
                byte[] bytes = is.readAllBytes();
                return Image.getInstance(bytes);
            }
        } catch (Exception e) {
            log.warn("Impossibile caricare logo {}: {}", LOGO_CLASSPATH, e.getMessage());
            return null;
        }
    }

    // ---------------- helpers layout (immutati) ----------------

    private void sectionTitle(Document doc, String text, Font font, Color accent) throws DocumentException {
        Paragraph p = new Paragraph(text, font);
        p.setSpacingAfter(3f);
        doc.add(p);

        PdfPTable line = new PdfPTable(1);
        line.setWidthPercentage(100);
        PdfPCell c = new PdfPCell(new Phrase(" "));
        c.setBorder(Rectangle.NO_BORDER);
        c.setFixedHeight(2.5f);
        c.setBackgroundColor(accent);
        line.addCell(c);

        doc.add(line);
        doc.add(new Paragraph(" "));
    }

    private PdfPTable simpleTable(float[] widths) {
        PdfPTable t = new PdfPTable(widths);
        t.setWidthPercentage(100);
        t.getDefaultCell().setBorder(Rectangle.NO_BORDER);
        t.setSpacingBefore(2f);
        t.setSpacingAfter(2f);
        return t;
    }

    private void addRow(PdfPTable t, String left, String right, Font leftFont, Font rightFont) {
        PdfPCell c1 = new PdfPCell(new Phrase(left, leftFont));
        c1.setBorder(Rectangle.NO_BORDER);
        c1.setPadding(1f);

        PdfPCell c2 = new PdfPCell(new Phrase(right, rightFont));
        c2.setBorder(Rectangle.NO_BORDER);
        c2.setHorizontalAlignment(Element.ALIGN_RIGHT);
        c2.setPadding(1f);

        t.addCell(c1);
        t.addCell(c2);
    }

    private Paragraph kvLine(String k, String v, Font kFont, Font vFont) {
        Phrase ph = new Phrase();
        ph.add(new Chunk(k + " ", kFont));
        ph.add(new Chunk(v, vFont));
        Paragraph p = new Paragraph(ph);
        p.setSpacingAfter(2f);
        return p;
    }

    private PdfPTable boxedTable(Color border, Color bg) {
        PdfPTable direct = new PdfPTable(1);
        direct.setWidthPercentage(100);
        direct.getDefaultCell().setBorder(Rectangle.BOX);
        direct.getDefaultCell().setBorderColor(border);
        direct.getDefaultCell().setBackgroundColor(bg);
        direct.getDefaultCell().setPadding(6f);
        return direct;
    }

    private PdfPCell boxLine(String text, Font f) {
        PdfPCell cell = new PdfPCell(new Phrase(text, f));
        cell.setBorder(Rectangle.NO_BORDER);
        cell.setPadding(2f);
        return cell;
    }

    private String nvl(String s) { return s == null ? "" : s; }

    private BigDecimal bd(BigDecimal v) { return v == null ? BigDecimal.ZERO : v; }

    private int safeInt(Integer v) { return v == null ? 0 : v; }

    private String eur(BigDecimal v) {
        BigDecimal x = bd(v).setScale(2, RoundingMode.HALF_UP);
        return x.toPlainString() + " €";
    }

    private String pct(BigDecimal v) {
        BigDecimal x = bd(v).setScale(0, RoundingMode.HALF_UP);
        return x.toPlainString() + "%";
    }

    private String safeFilename(String input) {
        String s = Normalizer.normalize(input, Normalizer.Form.NFD).replaceAll("\\p{M}", "");
        s = s.replaceAll("[^a-zA-Z0-9\\-_ ]", "").trim();
        s = s.replaceAll("\\s+", "_");
        if (s.isBlank()) s = "previsione";
        return s;
    }
    private void addGoldHeaderBar(Document doc, Color gold, Font titleFont) throws Exception {

        // 3 colonne per avere il titolo realmente centrato
        PdfPTable barTable = new PdfPTable(new float[]{20f, 60f, 20f});
        barTable.setWidthPercentage(100);

        // ===== COLONNA 1 — LOGO =====
        PdfPCell logoCell = new PdfPCell();
        logoCell.setBorder(Rectangle.NO_BORDER);
        logoCell.setBackgroundColor(gold);
        logoCell.setVerticalAlignment(Element.ALIGN_MIDDLE);
        logoCell.setPaddingLeft(10f);
        logoCell.setPaddingTop(6f);
        logoCell.setPaddingBottom(6f);

        Image logo = loadLogoOrNull();
        if (logo != null) {
            logo.scaleToFit(38f, 38f);
            logoCell.addElement(logo);
        } else {
            logoCell.addElement(new Phrase(" "));
        }

        barTable.addCell(logoCell);

        // ===== COLONNA 2 — TITOLO CENTRATO =====
        PdfPCell titleCell = new PdfPCell();
        titleCell.setBorder(Rectangle.NO_BORDER);
        titleCell.setBackgroundColor(gold);
        titleCell.setVerticalAlignment(Element.ALIGN_MIDDLE);

        Paragraph title = new Paragraph("PROPOSTA GESTIONE ONLINE", titleFont);
        title.setAlignment(Element.ALIGN_CENTER);
        title.setSpacingAfter(0f);

        titleCell.addElement(title);
        barTable.addCell(titleCell);

        // ===== COLONNA 3 — SPACER =====
        PdfPCell spacer = new PdfPCell(new Phrase(" "));
        spacer.setBorder(Rectangle.NO_BORDER);
        spacer.setBackgroundColor(gold);
        barTable.addCell(spacer);

        doc.add(barTable);

        // spazio ridotto sotto la barra
        doc.add(new Paragraph(" "));
    }


    private void addApartmentHeader(Document doc, PrevisioneGuadagno p, Font nameFont, Font addrFont) throws DocumentException {
        Paragraph name = new Paragraph(nvl(p.getNomeAppartamento()).toUpperCase(Locale.ITALIAN), nameFont);
        name.setAlignment(Element.ALIGN_CENTER);
        name.setSpacingAfter(2f);
        doc.add(name);

        String indirizzo = nvl(p.getIndirizzo()).trim();
        if (!indirizzo.isBlank()) {
            Paragraph addr = new Paragraph(indirizzo, addrFont);
            addr.setAlignment(Element.ALIGN_CENTER);
            addr.setSpacingAfter(2f);
            doc.add(addr);
        } else {
            // stesso “respiro” anche se indirizzo mancante
            doc.add(new Paragraph(" "));
        }
    }


    // Footer con pagina (immutato)
    static class PageFooter extends PdfPageEventHelper {
        @Override
        public void onEndPage(PdfWriter writer, Document document) {
            PdfContentByte cb = writer.getDirectContent();
            Font f = new Font(Font.HELVETICA, 8, Font.NORMAL, new Color(120,120,120));
            Phrase p = new Phrase("HostManager360 • Pagina " + writer.getPageNumber(), f);
            ColumnText.showTextAligned(cb, Element.ALIGN_CENTER, p,
                    (document.right() + document.left()) / 2,
                    document.bottom() - 18, 0);
        }
    }
}
