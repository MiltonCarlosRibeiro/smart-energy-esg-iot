package com.esg.iot.smartenergy.repo;

import com.esg.iot.smartenergy.domain.Device;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.util.List;

public interface DeviceRepository extends MongoRepository<Device, String> {
    List<Device> findByRoom(String room);
    boolean existsByName(String name);
}
