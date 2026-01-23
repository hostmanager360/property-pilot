package com.propertypilot.coreservice.dto;

import lombok.*;
import org.antlr.v4.runtime.misc.NotNull;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CreateTenantAppartamentoDTO {

    @NotNull
    private int appartamentoId;

    @NotNull
    private String tenantKey;
}

