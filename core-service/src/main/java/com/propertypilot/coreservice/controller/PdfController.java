package com.propertypilot.coreservice.controller;

import com.propertypilot.coreservice.dto.PdfResult;
import com.propertypilot.coreservice.service.PdfService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/core/private/pdf")
@RequiredArgsConstructor
public class PdfController {

    private final PdfService pdfService;

    @GetMapping(value = "/downloadPrevisione", produces = MediaType.APPLICATION_PDF_VALUE)
    public ResponseEntity<Resource> downloadPrevisione(@RequestParam("id") Integer id) {

        PdfResult pdf = pdfService.generatePrevisionePdf(id);

        if (pdf.getBytes() == null || pdf.getBytes().length == 0) {
            throw new IllegalStateException("PDF generato vuoto per previsione id=" + id);
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_PDF);
        headers.setContentDisposition(ContentDisposition.attachment().filename(pdf.getFilename()).build());
        headers.setCacheControl("no-cache, no-store, must-revalidate");
        headers.setPragma("no-cache");
        headers.setExpires(0);

        return ResponseEntity.ok()
                .headers(headers)
                .contentLength(pdf.getBytes().length)
                .body(new ByteArrayResource(pdf.getBytes()));
    }
}

