package com.esg.iot.smartenergy.web;

import com.esg.iot.smartenergy.domain.Device;
import com.esg.iot.smartenergy.repo.DeviceRepository;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;

@RestController
@RequestMapping("/api/devices")
public class DeviceController {

    private final DeviceRepository repo;

    public DeviceController(DeviceRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public List<Device> list(@RequestParam(required = false) String room) {
        return (room == null || room.isBlank()) ? repo.findAll() : repo.findByRoom(room);
    }

    @GetMapping("{id}")
    public ResponseEntity<Device> get(@PathVariable String id) {
        return repo.findById(id).map(ResponseEntity::ok).orElse(ResponseEntity.notFound().build());
    }

    @PostMapping
    public ResponseEntity<?> create(@RequestBody @Valid Device d) {
        if (repo.existsByName(d.getName()))
            return ResponseEntity.unprocessableEntity().body("Device name already exists");
        var saved = repo.save(d);
        return ResponseEntity.created(URI.create("/api/devices/" + saved.getId())).body(saved);
    }

    @PutMapping("{id}")
    public ResponseEntity<?> update(@PathVariable String id, @RequestBody @Valid Device d) {
        return repo.findById(id).map(found -> {
            found.setName(d.getName());
            found.setRoom(d.getRoom());
            return ResponseEntity.ok(repo.save(found));
        }).orElse(ResponseEntity.notFound().build());
    }

    @DeleteMapping("{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        if (!repo.existsById(id)) return ResponseEntity.notFound().build();
        repo.deleteById(id);
        return ResponseEntity.noContent().build();
    }
}
