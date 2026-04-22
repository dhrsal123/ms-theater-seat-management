package io.cinema.mstheaterseatmanagement.service.impl;

import io.cinema.mstheaterseatmanagement.domain.dto.TheaterDto;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterRowProjection;
import io.cinema.mstheaterseatmanagement.repository.TheaterRepository;
import io.cinema.mstheaterseatmanagement.service.TheaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

import static io.cinema.mstheaterseatmanagement.mapper.TheaterProjectionMapper.toTheaterDto;

@Slf4j
@Service
@RequiredArgsConstructor
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;

    public Flux<TheaterDto> getAllTheaters(Integer page, Integer size) {

        return theaterRepository
                .findAllTheaterDetails(size, (long) page * size)
                .filter(projection -> Objects.nonNull(projection.theaterId()))
                .groupBy(TheaterRowProjection::theaterId)
                .flatMap(theaterGroup ->
                        theaterGroup
                                .collectList()
                                .mapNotNull(rows -> {
                                    if (!rows.isEmpty()) {
                                        return toTheaterDto(rows, rows.getFirst().theaterId());
                                    }
                                    return null;
                                })
                );

    }

    @Override
    public Mono<TheaterDto> getTheaterById(UUID theaterId) {
        return theaterRepository
                .findTheaterDetailsById(theaterId)
                .filter(projection -> Objects.nonNull(projection.theaterId()))
                .collectList()
                .mapNotNull(projections -> toTheaterDto(projections, theaterId));
    }

}
