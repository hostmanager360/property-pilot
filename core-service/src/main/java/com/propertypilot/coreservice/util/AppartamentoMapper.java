package com.propertypilot.coreservice.util;

import com.propertypilot.coreservice.dto.AppartamentoDettagliDTO;
import com.propertypilot.coreservice.model.Appartamento;
import com.propertypilot.coreservice.model.DettagliAppartamento;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

@Mapper(componentModel = "spring")
public interface AppartamentoMapper {

    @Mapping(source = "appartamento.id", target = "id")
    @Mapping(source = "appartamento.nomeAppartamento", target = "nomeAppartamento")
    @Mapping(source = "appartamento.via", target = "via")
    @Mapping(source = "appartamento.civico", target = "civico")
    @Mapping(source = "appartamento.citta", target = "citta")
    @Mapping(source = "appartamento.stato", target = "stato")
    @Mapping(source = "appartamento.cup", target = "cup")
    @Mapping(source = "appartamento.cin", target = "cin")
    @Mapping(source = "appartamento.cir", target = "cir")
    @Mapping(source = "appartamento.createdAt", target = "createdAt")
    @Mapping(source = "appartamento.updateAt", target = "updatedAt")
    @Mapping(source = "dettagli.dataInizioAttivita", target = "dataInizioAttivita")
    @Mapping(source = "dettagli.costoPulizie", target = "costoPulizie")
    @Mapping(source = "dettagli.costoUtenzeGiornaliere", target = "costoUtenzeGiornaliere")
    @Mapping(source = "dettagli.costoTassaSoggiornoPerOspite", target = "costoTassaSoggiornoPerOspite")
    @Mapping(source = "dettagli.tassaSoggiornoAutomatica", target = "tassaSoggiornoAutomatica")
    @Mapping(source = "dettagli.cedolareSecca", target = "cedolareSecca")
    @Mapping(source = "dettagli.percentualeCommissionePm", target = "percentualeCommissionePm")
    @Mapping(source = "dettagli.percentualeCommissioneHost", target = "percentualeCommissioneHost")
    @Mapping(source = "dettagli.percentualeCommissioneTotale", target = "percentualeCommissioneTotale")
    AppartamentoDettagliDTO toDTO(Appartamento appartamento, DettagliAppartamento dettagli);
}
