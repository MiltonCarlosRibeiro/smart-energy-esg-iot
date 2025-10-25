package com.esg.iot.smartenergy.web;

import com.esg.iot.smartenergy.domain.Reading;
import com.esg.iot.smartenergy.repo.ReadingRepository;
import com.esg.iot.smartenergy.web.dto.TelemetryDTO;
import jakarta.validation.Valid;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ReadingController {

    private final ReadingRepository repo;

    public ReadingController(ReadingRepository repo) {
        this.repo = repo;
    }

    @PostMapping("/telemetry")
    public ResponseEntity<Reading> telemetry(@RequestBody @Valid TelemetryDTO dto) {
        var ts = (dto.ts() != null && !dto.ts().isBlank()) ? Instant.parse(dto.ts()) : Instant.now();
        var saved = repo.save(Reading.builder()
                .deviceId(dto.deviceId())
                .room(dto.room())
                .presence(dto.presence())
                .lightOn(dto.lightOn())
                .temperature(dto.temperature())
                .ts(ts)
                .build());
        return ResponseEntity.ok(saved);
    }

    @GetMapping("/readings/latest")
    public ResponseEntity<Reading> latest(@RequestParam String room) {
        return repo.findTopByRoomOrderByTsDesc(room)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.noContent().build());
    }

    @GetMapping("/readings")
    public List<Reading> history(
            @RequestParam String room,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant from,
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE_TIME) Instant to) {
        return repo.findByRoomAndTsBetweenOrderByTsAsc(room, from, to);
    }
}
