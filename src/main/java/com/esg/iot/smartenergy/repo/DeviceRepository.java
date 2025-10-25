package com.esg.iot.smartenergy.repo;

import com.esg.iot.smartenergy.domain.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.Optional;

public interface DeviceRepository extends MongoRepository<Device, String> {
    Optional<Device> findByName(String name);
}
