package com.esg.iot.smartenergy.web;

import com.esg.iot.smartenergy.domain.Device;
import com.esg.iot.smartenergy.repo.DeviceRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.Instant;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceRepository repo;

    public DeviceController(DeviceRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Device> all() {
        return repo.findAll();
    }

    @PostMapping
    public ResponseEntity<Device> create(@Valid @RequestBody Device d) {
        // Se o payload veio sem createdAt, define agora
        Instant created = (d.createdAt() == null) ? Instant.now() : d.createdAt();

        Device toSave = new Device(
                null,           // id será gerado pelo Mongo
                d.name(),
                d.room(),
                created
        );
        Device saved = repo.save(toSave);
        return ResponseEntity
                .created(URI.create("/api/devices/" + saved.id()))
                .body(saved);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Device> byId(@PathVariable String id) {
        return repo.findById(id)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @PutMapping("/{id}")
    public ResponseEntity<Device> update(@PathVariable String id, @Valid @RequestBody Device d) {
        return repo.findById(id)
                .map(existing -> {
                    // mantém createdAt original; atualiza name/room
                    Device updated = new Device(
                            existing.id(),
                            d.name(),
                            d.room(),
                            existing.createdAt()
                    );
                    return ResponseEntity.ok(repo.save(updated));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
