package io.cinema.mstheaterseatmanagement.service.impl;

import io.cinema.domain.enumerated.CinemaExceptionTypes;
import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.dto.request.TheaterRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursResponseDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.TheaterResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.AddressEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.OperatingHoursEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterRowProjection;
import io.cinema.mstheaterseatmanagement.repository.AddressRepository;
import io.cinema.mstheaterseatmanagement.repository.OperatingHoursRepository;
import io.cinema.mstheaterseatmanagement.repository.TheaterRepository;
import io.cinema.mstheaterseatmanagement.service.TheaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.Objects;
import java.util.UUID;

import static io.cinema.mstheaterseatmanagement.mapper.TheaterMapper.toTheaterDto;
import static io.cinema.mstheaterseatmanagement.mapper.TheaterMapper.toTheaterEntity;
import static io.cinema.mstheaterseatmanagement.utils.AddressUtils.getAddress;

@Slf4j
@Service
@RequiredArgsConstructor
public class TheaterServiceImpl implements TheaterService {

    private final TheaterRepository theaterRepository;
    private final AddressRepository addressRepository;
    private final TransactionalOperator transactionalOperator;
    private final OperatingHoursRepository operatingHoursRepository;


    @Override
    public Flux<TheaterResponseDto> getAllTheaters(Integer page, Integer size) {

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
                ).as(transactionalOperator::transactional);

    }

    @Override
    public Mono<TheaterResponseDto> getTheaterById(UUID theaterId) {
        return theaterRepository
                .findTheaterDetailsById(theaterId)
                .filter(projection -> Objects.nonNull(projection.theaterId()))
                .collectList()
                .mapNotNull(projections -> toTheaterDto(projections, theaterId))
                .as(transactionalOperator::transactional);
    }

    @Override
    public Mono<TheaterResponseDto> createTheater(TheaterRequestDto theaterRequestDto) {

        var addressDto = theaterRequestDto.address();
        var addressEntity = AddressEntity.builder()
                .street(addressDto.street())
                .city(addressDto.city())
                .state(addressDto.state())
                .country(addressDto.country())
                .zip(addressDto.zip())
                .build();

        return addressRepository.save(addressEntity)
                .flatMap(savedAddress -> {

                    var theaterEntity = toTheaterEntity(savedAddress.getId(), theaterRequestDto);

                    return theaterRepository.save(theaterEntity)
                            .flatMap(savedTheater ->
                                    Flux.fromIterable(theaterRequestDto.operatingHours())
                                            .filter(Objects::nonNull)
                                            .flatMap(ohDto -> {
                                                var operatingHoursEntity = OperatingHoursEntity
                                                        .builder()
                                                        .dayOfWeek(ohDto.dayOfWeek())
                                                        .startTime(ohDto.start())
                                                        .endTime(ohDto.end())
                                                        .theaterId(savedTheater.getId())
                                                        .build();

                                                return operatingHoursRepository.save(operatingHoursEntity);
                                            })
                                            .collectList()
                                            .map(savedHours -> {
                                                var operatingHoursResponse = savedHours.stream()
                                                        .map(h -> new OperatingHoursResponseDto(
                                                                h.getDayOfWeek(),
                                                                h.getStartTime(),
                                                                h.getEndTime()))
                                                        .toList();

                                                UUID id = savedTheater.getId();
                                                return TheaterResponseDto.builder()
                                                        .theaterId(id.toString())
                                                        .name(savedTheater.getName())
                                                        .email(savedTheater.getEmail())
                                                        .phone(savedTheater.getPhone())
                                                        .location(getAddress(savedAddress))
                                                        .operatingHours(operatingHoursResponse)
                                                        .build();
                                            })
                            );

                })
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to create theater: {}", e.getMessage()))
                .onErrorMap(
                        CinemaException.class,
                        e -> new CinemaException(
                                "DB error during theater creation",
                                CinemaExceptionTypes.TECHNICAL_ERROR
                        )
                );
    }

    @Override
    public Mono<Void> deleteTheater(UUID theaterId) {
        return theaterRepository.findById(theaterId)
                .switchIfEmpty(Mono.error(new CinemaException(
                        "Theater with ID " + theaterId + " not found",
                        CinemaExceptionTypes.BAD_REQUEST
                )))
                .flatMap(theater ->
                        operatingHoursRepository.deleteAllByTheaterId(theaterId)
                                .then(theaterRepository.delete(theater))
                                .then(addressRepository.deleteById(theater.getAddressId()))
                )
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to delete theater with ID {}: {}", theaterId, e.getMessage()))
                .onErrorMap(
                        throwable -> !(throwable instanceof CinemaException),
                        e -> new CinemaException(
                                "DB error during theater deletion",
                                CinemaExceptionTypes.TECHNICAL_ERROR
                        )
                );
    }
}
