package io.cinema.mstheaterseatmanagement.service;

import io.cinema.mstheaterseatmanagement.domain.dto.TheaterDto;
import reactor.core.publisher.Flux;

public interface TheaterService {

    Flux<TheaterDto> getAllTheaters(Integer page, Integer size);
}
