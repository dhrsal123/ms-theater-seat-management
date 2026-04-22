package io.cinema.mstheaterseatmanagement.service;

import io.cinema.mstheaterseatmanagement.domain.dto.TheaterDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TheaterService {

    Flux<TheaterDto> getAllTheaters(Integer page, Integer size);

    Mono<TheaterDto> getTheaterById(UUID theaterId);
}
