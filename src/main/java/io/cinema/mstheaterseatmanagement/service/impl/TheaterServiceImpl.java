package io.cinema.mstheaterseatmanagement.service.impl;

import io.cinema.mstheaterseatmanagement.domain.dto.OperatingHoursDto;
import io.cinema.mstheaterseatmanagement.domain.dto.TheaterDto;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterRowProjection;
import io.cinema.mstheaterseatmanagement.repository.TheaterRepository;
import io.cinema.mstheaterseatmanagement.service.TheaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import reactor.core.publisher.Flux;

import java.util.Objects;

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
                                    if (rows.isEmpty()) {
                                        return null;
                                    }

                                    var base = rows.getFirst();
                                    var hoursList = rows.stream()
                                            .filter(r -> r.dayOfWeek() != null)
                                            .map(projection ->
                                                    new OperatingHoursDto(
                                                            projection.dayOfWeek(),
                                                            projection.startTime(),
                                                            projection.endTime()
                                                    ))
                                            .toList();

                                    var address = String.format(
                                            "%s, %s, %s, ZIP Code: %s",
                                            base.street(),
                                            base.city(),
                                            base.state(),
                                            base.zip()
                                    );

                                    return new TheaterDto(
                                            base.name(),
                                            base.phone(),
                                            address,
                                            hoursList
                                    );

                                })
                );

    }

}
