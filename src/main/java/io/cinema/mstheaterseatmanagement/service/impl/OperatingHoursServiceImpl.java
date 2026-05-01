package io.cinema.mstheaterseatmanagement.service.impl;

import io.cinema.domain.enumerated.CinemaExceptionTypes;
import io.cinema.domain.exceptions.CinemaException;
import io.cinema.mstheaterseatmanagement.domain.dto.request.OperatingHoursRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursInfoResponseDto;
import io.cinema.mstheaterseatmanagement.domain.entity.OperatingHoursEntity;
import io.cinema.mstheaterseatmanagement.repository.OperatingHoursRepository;
import io.cinema.mstheaterseatmanagement.repository.TheaterRepository;
import io.cinema.mstheaterseatmanagement.service.OperatingHoursService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.reactive.TransactionalOperator;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

import static io.cinema.domain.enumerated.CinemaExceptionTypes.TECHNICAL_ERROR;

@Slf4j
@Service
@RequiredArgsConstructor
public class OperatingHoursServiceImpl implements OperatingHoursService {
    private final TheaterRepository theaterRepository;
    private final TransactionalOperator transactionalOperator;
    private final OperatingHoursRepository operatingHoursRepository;

    @Override
    public Flux<OperatingHoursInfoResponseDto> getTheaterOperatingHours(UUID theaterId) {
        return operatingHoursRepository
                .findOperatingHoursByTheaterId(theaterId)
                .map(oH ->
                        new OperatingHoursInfoResponseDto(
                                oH.getId(),
                                oH.getDayOfWeek(),
                                oH.getStartTime(),
                                oH.getEndTime()
                        )
                ).as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to find operating hours: {}", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<OperatingHoursInfoResponseDto> saveTheaterOperatingHours(
            UUID theaterId,
            OperatingHoursRequestDto operatingHoursRequest
    ) {

        return theaterRepository
                .findById(theaterId)
                .switchIfEmpty(Mono.error(
                        new CinemaException(
                                "The theater does not exist",
                                CinemaExceptionTypes.BAD_REQUEST
                        )
                ))
                .flatMap(theater -> {
                    var operatingHours = OperatingHoursEntity.builder()
                            .dayOfWeek(operatingHoursRequest.dayOfWeek())
                            .startTime(operatingHoursRequest.start())
                            .endTime(operatingHoursRequest.end())
                            .theaterId(theaterId)
                            .build();

                    return operatingHoursRepository.save(operatingHours);
                })
                .map(oH -> new OperatingHoursInfoResponseDto(
                        oH.getId(),
                        oH.getDayOfWeek(),
                        oH.getStartTime(),
                        oH.getEndTime()
                ))
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to find operating hours: {}", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during read", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<OperatingHoursInfoResponseDto> updateOperatingHours(
            UUID operatingHoursId,
            OperatingHoursRequestDto operatingHoursInfo
    ) {
        return operatingHoursRepository
                .findById(operatingHoursId)
                .switchIfEmpty(Mono.error(new CinemaException(
                        "The operating hour was not found.",
                        CinemaExceptionTypes.BAD_REQUEST
                )))
                .flatMap(oH -> {
                            oH.setDayOfWeek(operatingHoursInfo.dayOfWeek());
                            oH.setStartTime(operatingHoursInfo.start());
                            oH.setEndTime(operatingHoursInfo.end());

                            return operatingHoursRepository.save(oH);
                        }
                )
                .map(oH -> new OperatingHoursInfoResponseDto(
                        oH.getId(),
                        oH.getDayOfWeek(),
                        oH.getStartTime(),
                        oH.getEndTime()
                ))
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to update operating hours: {}", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during update", TECHNICAL_ERROR)
                );
    }

    @Override
    public Mono<Void> deleteOperatingHours(UUID operatingHoursId) {
        return operatingHoursRepository
                .deleteById(operatingHoursId)
                .as(transactionalOperator::transactional)
                .doOnError(e -> log.error("Failed to update operating hours: {}", e.getMessage()))
                .onErrorMap(
                        e -> !(e instanceof CinemaException),
                        e -> new CinemaException("DB error during update", TECHNICAL_ERROR)
                );
    }

}
