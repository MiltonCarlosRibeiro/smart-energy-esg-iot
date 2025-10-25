package com.esg.iot.smartenergy.web.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record TelemetryDTO(
        @NotBlank String deviceId,
        @NotBlank String room,
        @NotNull Boolean presence,
        @NotNull Boolean lightOn,
        Double temperature,
        String ts // ISO-8601 opcional; se null, o backend usa Instant.now()
) {}
