package io.cinema.mstheaterseatmanagement.repository;

import io.cinema.mstheaterseatmanagement.domain.entity.OperatingHoursEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface OperatingHoursRepository extends ReactiveCrudRepository<OperatingHoursEntity, UUID> {
    Flux<OperatingHoursEntity> findOperatingHoursByTheaterId(UUID theaterId);
}
