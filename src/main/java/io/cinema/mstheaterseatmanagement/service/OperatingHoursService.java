package io.cinema.mstheaterseatmanagement.service;

import io.cinema.mstheaterseatmanagement.domain.dto.request.OperatingHoursRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursInfoResponseDto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

public interface OperatingHoursService {
    Flux<OperatingHoursInfoResponseDto> getTheaterOperatingHours(UUID theaterId);

    Flux<OperatingHoursInfoResponseDto> saveTheaterOperatingHours(
            UUID theaterId,
            List<OperatingHoursRequestDto> operatingHoursRequest
    );


    Mono<OperatingHoursInfoResponseDto> updateOperatingHours(
            UUID operatingHoursId,
            OperatingHoursRequestDto operatingHoursInfo
    );

    Mono<Void> deleteOperatingHours(UUID operatingHoursId);
}
