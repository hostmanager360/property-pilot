package com.propertypilot.coreservice.service;

import com.propertypilot.coreservice.dto.PrevisioneGuadagnoDto;
import com.propertypilot.coreservice.dto.PrevisioneGuadagnoListDto;
import com.propertypilot.coreservice.model.PrevisioneGuadagno;

import java.util.List;

public interface PrevisioneGadagnoService {
    PrevisioneGuadagno calcoloCostiPrevisioneGadagno(PrevisioneGuadagnoDto dto);
    List<PrevisioneGuadagnoListDto> getAllPrevisioni();
    PrevisioneGuadagnoDto getDettaglioById(Long previsioneId);
    void deleteById(Long id);
}
