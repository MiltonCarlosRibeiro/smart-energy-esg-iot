// Device.java
package com.esg.iot.smartenergy.domain;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.Instant;

@Document("devices")
public record Device(
        @Id String id,
        String name,
        String room,
        Instant createdAt
) {}
