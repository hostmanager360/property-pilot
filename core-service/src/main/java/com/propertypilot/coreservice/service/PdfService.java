package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.dto.PdfResult;

public interface PdfService {
    PdfResult generatePrevisionePdf(Integer id);
}
