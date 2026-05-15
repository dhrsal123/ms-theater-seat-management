package io.cinema.mstheaterseatmanagement.service.impl;

import io.cinema.domain.enumerated.CinemaExceptionTypes;
import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.dto.request.OperatingHoursRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursInfoResponseDto;
import io.cinema.mstheaterseatmanagement.mapper.OperatingHoursMapper;
import io.cinema.mstheaterseatmanagement.repository.OperatingHoursRepository;
import io.cinema.mstheaterseatmanagement.repository.TheaterRepository;
import io.cinema.mstheaterseatmanagement.service.OperatingHoursService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.BAD_REQUEST;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.NOT_FOUND;
import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperatingHoursServiceImpl implements OperatingHoursService {
    private final TheaterRepository theaterRepository;
    private final TransactionalOperator transactionalOperator;
    private final OperatingHoursRepository operatingHoursRepository;
    private final OperatingHoursMapper operatingHoursMapper;

    @Override
    public Flux<OperatingHoursInfoResponseDto> getTheaterOperatingHours(UUID theaterId) {
        return operatingHoursRepository
                .findOperatingHoursByTheaterId(theaterId)
                .map(operatingHoursMapper::toInfoResponseDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to find operating hours for theater: {}: {}", theaterId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }

    @Override
    public Flux<OperatingHoursInfoResponseDto> saveTheaterOperatingHours(
            UUID theaterId,
            List<OperatingHoursRequestDto> operatingHoursRequest
    ) {
        return theaterRepository
                .findById(theaterId)
                .switchIfEmpty(Mono.error(
                        new CinemaException(
                                "The theater does not exist",
                                CinemaExceptionTypes.BAD_REQUEST
                        )
                ))
                .flatMapMany(theater -> {
                    var operatingHours = operatingHoursRequest.stream()
                            .map(dto -> operatingHoursMapper.toEntity(dto, theaterId))
                            .toList();

                    return operatingHoursRepository.saveAll(operatingHours);
                })
                .map(operatingHoursMapper::toInfoResponseDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to save operating hours for theater {}: {}", theaterId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during save", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<OperatingHoursInfoResponseDto> updateOperatingHours(
            UUID theaterId,
            UUID operatingHoursId,
            OperatingHoursRequestDto operatingHoursInfo
    ) {
        return validateOperatingHoursBelongsToTheater(theaterId, operatingHoursId)
                .then(operatingHoursRepository.findById(operatingHoursId))
                .flatMap(oH -> {
                    operatingHoursMapper.updateEntityFromDto(operatingHoursInfo, oH);
                    return operatingHoursRepository.save(oH);
                })
                .map(operatingHoursMapper::toInfoResponseDto)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to update operating hours {}: {}", operatingHoursId, e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during update", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<Void> deleteOperatingHours(
            UUID theaterId,
            UUID operatingHoursId
    ) {
        return validateOperatingHoursBelongsToTheater(theaterId, operatingHoursId)
                .then(operatingHoursRepository.deleteById(operatingHoursId))
                .as(transactionalOperator::transactional)
                .doOnError(e ->
                        log.error("Failed to delete operating hours {}: {}", operatingHoursId, e.getMessage())
                )
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during delete", TECHNICAL_ERROR)
                );
    }

    // private methods
    private Mono<Void> validateOperatingHoursBelongsToTheater(UUID theaterId, UUID operatingHoursId) {
        return operatingHoursRepository.findById(operatingHoursId)
                .switchIfEmpty(Mono.error(new CinemaException("Operating hour not found", NOT_FOUND)))
                .flatMap(operatingHoursEntity -> {
                    if (!operatingHoursEntity.getTheaterId().equals(theaterId)) {
                        return Mono.error(new CinemaException(
                                "Operating hours specified does not belong to the specified theater",
                                BAD_REQUEST
                        ));
                    }
                    return Mono.empty();
                });
    }
}