package io.cinema.mstheaterseatmanagement.repository;

import io.cinema.mstheaterseatmanagement.domain.entity.AddressEntity;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;

import java.util.UUID;

public interface AddressRepository extends ReactiveCrudRepository<AddressEntity, UUID> {
}
