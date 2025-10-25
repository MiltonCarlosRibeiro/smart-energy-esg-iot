package com.esg.iot.smartenergy.repo;

import com.esg.iot.smartenergy.domain.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;
import java.util.Optional;

public interface DeviceRepository extends MongoRepository<Device, String> {

    // Pode existir mais de um device com o mesmo name (situação atual)
    List<Device> findByName(String name);

    // Para pegar só “um”, sem estourar exceção, usamos ordenação por createdAt
    Optional<Device> findFirstByNameOrderByCreatedAtDesc(String name);

    // Útil para limpeza de duplicatas se precisar
    long deleteByName(String name);
}
