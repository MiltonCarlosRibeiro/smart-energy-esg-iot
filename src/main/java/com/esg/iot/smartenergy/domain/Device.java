package com.esg.iot.smartenergy.domain;

import lombok.*;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import jakarta.validation.constraints.NotBlank;
import java.time.Instant;

@Document("devices")
@Data @NoArgsConstructor @AllArgsConstructor @Builder
public class Device {
    @Id private String id;

    @Indexed(unique = true)
    @NotBlank private String name;

    @NotBlank private String room;

    @Builder.Default
    private Instant createdAt = Instant.now();
}
