package io.cinema.mstheaterseatmanagement.service;

import io.cinema.mstheaterseatmanagement.domain.dto.request.TheaterRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.TheaterResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

public interface TheaterService {

    Flux<TheaterResponseDto> getAllTheaters(Integer page, Integer size);

    Mono<TheaterResponseDto> getTheaterById(UUID theaterId);

    Mono<TheaterResponseDto> createTheater(TheaterRequestDto theaterRequestDto);
}
