package com.propertypilot.coreservice.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record SendPrevisioneEmailRequest(
        @NotNull Integer previsioneId,
        @NotBlank @Email String ownerEmail,
        @NotBlank(message = "Nome proprietario obbligatorio")
                String ownerName
) {}