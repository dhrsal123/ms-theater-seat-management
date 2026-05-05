package io.cinema.mstheaterseatmanagement.repository;

import io.cinema.mstheaterseatmanagement.domain.entity.SeatEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface SeatRepository extends ReactiveCrudRepository<SeatEntity, UUID> {
    Flux<SeatEntity> findAllByRoomId(UUID roomId);
}
