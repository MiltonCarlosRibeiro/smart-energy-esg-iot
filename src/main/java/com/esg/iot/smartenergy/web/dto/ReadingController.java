package com.esg.iot.smartenergy.web;

import com.esg.iot.smartenergy.domain.Device;
import com.esg.iot.smartenergy.domain.Reading;
import com.esg.iot.smartenergy.repo.DeviceRepository;
import com.esg.iot.smartenergy.repo.ReadingRepository;
import com.esg.iot.smartenergy.web.dto.TelemetryDTO;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.format.DateTimeParseException;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReadingController {

    private final ReadingRepository readingRepo;
    private final DeviceRepository deviceRepo;

    public ReadingController(ReadingRepository readingRepo, DeviceRepository deviceRepo) {
        this.readingRepo = readingRepo;
        this.deviceRepo = deviceRepo;
    }

    /** Envia telemetria: POST /api/telemetry */
    @PostMapping("/telemetry")
    public ResponseEntity<Reading> ingest(@Valid @RequestBody TelemetryDTO dto) {
        // upsert simples de Device pelo "name" = deviceId
        deviceRepo.findByName(dto.deviceId()).orElseGet(() ->
                deviceRepo.save(new Device(
                        null,
                        dto.deviceId(),
                        dto.room(),
                        Instant.now()
                ))
        );

        Instant ts = Instant.now();
        if (dto.ts() != null && !dto.ts().isBlank()) {
            try { ts = Instant.parse(dto.ts()); } catch (DateTimeParseException ignored) {}
        }

        Reading r = new Reading(
                null,
                dto.deviceId(),
                dto.room(),
                Boolean.TRUE.equals(dto.presence()),
                Boolean.TRUE.equals(dto.lightOn()),
                dto.temperature(),
                ts
        );

        return ResponseEntity.ok(readingRepo.save(r));
    }

    /** Última leitura por sala: GET /api/readings/latest?room=Sala%20101 */
    @GetMapping("/readings/latest")
    public ResponseEntity<Reading> latest(@RequestParam String room) {
        return readingRepo.findTopByRoomOrderByTsDesc(room)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    /** Histórico simples: GET /api/readings?room=Sala%20101 */
    @GetMapping("/readings")
    public List<Reading> history(@RequestParam String room) {
        return readingRepo.findByRoomOrderByTsDesc(room);
    }
}
