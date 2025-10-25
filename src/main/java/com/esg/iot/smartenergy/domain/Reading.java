package com.esg.iot.smartenergy.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.*;
import java.time.Instant;

@Document("readings")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Reading {
    @Id private String id;

    @NotBlank private String deviceId;
    @NotBlank private String room;

    @NotNull private Boolean presence;
    @NotNull private Boolean lightOn;
    private Double temperature;

    @Indexed
    @Builder.Default
    private Instant ts = Instant.now();
}
