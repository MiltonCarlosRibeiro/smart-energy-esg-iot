package com.esg.iot.smartenergy.repo;

import com.esg.iot.smartenergy.domain.Reading;
import org.springframework.data.mongodb.repository.MongoRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface ReadingRepository extends MongoRepository<Reading, String> {
    Optional<Reading> findTopByRoomOrderByTsDesc(String room);
    List<Reading> findByRoomAndTsBetweenOrderByTsAsc(String room, Instant from, Instant to);
}
