package io.cinema.mstheaterseatmanagement.service.impl;

import io.cinema.domain.enumerated.CinemaExceptionTypes;
import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.dto.request.TheaterRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.TheaterResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.OperatingHoursEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterRowProjection;
import io.cinema.mstheaterseatmanagement.mapper.AddressMapper;
import io.cinema.mstheaterseatmanagement.mapper.OperatingHoursMapper;
import io.cinema.mstheaterseatmanagement.mapper.TheaterMapper;
import io.cinema.mstheaterseatmanagement.repository.AddressRepository;
import io.cinema.mstheaterseatmanagement.repository.OperatingHoursRepository;
import io.cinema.mstheaterseatmanagement.repository.RoomRepository;
import io.cinema.mstheaterseatmanagement.repository.SeatRepository;
import io.cinema.mstheaterseatmanagement.repository.TheaterRepository;
import io.cinema.mstheaterseatmanagement.service.TheaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class TheaterServiceImpl implements TheaterService {

    private static final String THEATER_NOT_FOUND = "Theater not found";
    private final TheaterRepository theaterRepository;
    private final AddressRepository addressRepository;
    private final OperatingHoursRepository operatingHoursRepository;
    private final RoomRepository roomRepository;
    private final SeatRepository seatRepository;
    private final TransactionalOperator transactionalOperator;

    private final TheaterMapper theaterMapper;
    private final AddressMapper addressMapper;
    private final OperatingHoursMapper operatingHoursMapper;

    @Override
    public Flux<TheaterResponseDto> getAllTheaters(Integer page, Integer size) {
        return theaterRepository
                .findAllTheaterDetails(size, (long) page * size)
                .filter(projection -> Objects.nonNull(projection.theaterId()))
                .groupBy(TheaterRowProjection::theaterId)
                .flatMap(group -> group.collectList()
                        .map(rows -> theaterMapper.toTheaterDtoFromProjections(rows, group.key()))
                )
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to fetch all theaters: {}", e.getMessage()))
                .onErrorMap(e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error fetching theaters", TECHNICAL_ERROR));
    }

    @Override
    public Mono<TheaterResponseDto> getTheaterById(UUID theaterId) {
        return theaterRepository
                .findTheaterDetailsById(theaterId)
                .filter(projection -> Objects.nonNull(projection.theaterId()))
                .collectList()
                .filter(list -> !list.isEmpty())
                .switchIfEmpty(Mono.error(new CinemaException(THEATER_NOT_FOUND, CinemaExceptionTypes.NOT_FOUND)))
                .map(rows -> theaterMapper.toTheaterDtoFromProjections(rows, theaterId))
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to fetch theater {}: {}", theaterId, e.getMessage()))
                .onErrorMap(e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error fetching theater", TECHNICAL_ERROR));
    }

    @Override
    public Mono<TheaterResponseDto> createTheater(TheaterRequestDto dto) {
        return addressRepository.save(addressMapper.toEntity(dto.address()))
                .flatMap(savedAddress -> theaterRepository
                        .save(theaterMapper.toEntity(dto, savedAddress.getId()))
                        .flatMap(savedTheater -> saveOperatingHours(dto, savedTheater.getId())
                                .map(savedHours -> theaterMapper
                                        .toResponseDto(savedTheater, savedAddress, savedHours)
                                )
                        )
                )
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to create theater: {}", e.getMessage()))
                .onErrorMap(e -> !(e instanceof CinemaException),
                        e -> new CinemaException("Critical DB error during creation", TECHNICAL_ERROR));
    }

    @Override
    public Mono<TheaterResponseDto> updateTheater(UUID theaterId, TheaterRequestDto dto) {
        return theaterRepository.findById(theaterId)
                .switchIfEmpty(Mono.error(new CinemaException(THEATER_NOT_FOUND, CinemaExceptionTypes.NOT_FOUND)))
                .flatMap(theater -> addressRepository.findById(theater.getAddressId())
                        .flatMap(address -> {
                            addressMapper.updateEntityFromDto(dto.address(), address);
                            theaterMapper.updateEntityFromDto(dto, theater);

                            return addressRepository.save(address)
                                    .then(theaterRepository.save(theater))
                                    .flatMap(savedTheater -> refreshOperatingHours(dto, theaterId)
                                            .map(hours ->
                                                    theaterMapper
                                                            .toResponseDto(
                                                                    savedTheater,
                                                                    address,
                                                                    hours
                                                            )
                                            )
                                    );
                        })
                )
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Update failed for {}: {}", theaterId, e.getMessage()))
                .onErrorMap(e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during update", TECHNICAL_ERROR));
    }

    @Override
    public Mono<Void> deleteTheater(UUID theaterId) {
        return theaterRepository.findById(theaterId)
                .switchIfEmpty(Mono.error(new CinemaException(THEATER_NOT_FOUND, CinemaExceptionTypes.NOT_FOUND)))
                .flatMap(this::cascadeDeleteDependencies)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Deletion failed for {}: {}", theaterId, e.getMessage()))
                .onErrorMap(e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during deletion", TECHNICAL_ERROR));
    }


    // private methods
    private Mono<Void> cascadeDeleteDependencies(TheaterEntity theater) {
        return roomRepository.findRoomEntityByTheaterId(theater.getId())
                .flatMap(room -> seatRepository.findAllByRoomId(room.getId())
                        .flatMap(seat -> seatRepository.deleteById(seat.getId()))
                        .then(roomRepository.deleteById(room.getId()))
                )
                .then(operatingHoursRepository.deleteAllByTheaterId(theater.getId()))
                .then(theaterRepository.delete(theater))
                .then(addressRepository.deleteById(theater.getAddressId()));
    }

    private Mono<List<OperatingHoursEntity>> saveOperatingHours(TheaterRequestDto dto, UUID theaterId) {
        var operatingHours = dto.operatingHours();
        var hoursToSave = operatingHours.stream()
                .map(h -> operatingHoursMapper.toEntity(h, theaterId))
                .toList();

        return operatingHoursRepository.saveAll(hoursToSave).collectList();
    }

    private Mono<List<OperatingHoursEntity>> refreshOperatingHours(TheaterRequestDto dto, UUID theaterId) {
        return operatingHoursRepository.deleteAllByTheaterId(theaterId)
                .then(saveOperatingHours(dto, theaterId));
    }
}