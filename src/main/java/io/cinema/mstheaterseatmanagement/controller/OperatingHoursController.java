package io.cinema.mstheaterseatmanagement.controller;


import io.cinema.domain.annotations.HasManagerRole;
import io.cinema.mstheaterseatmanagement.domain.dto.request.OperatingHoursRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursInfoResponseDto;
import io.cinema.mstheaterseatmanagement.service.OperatingHoursService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/theaters/{theaterId}/operating-hours")
public class OperatingHoursController {
    private final OperatingHoursService operatingHoursService;

    @Cacheable(value = "operating_hours", key = "#theaterId")
    @GetMapping
    public Flux<OperatingHoursInfoResponseDto> getTheaterOperatingHours(
            @PathVariable @NotNull UUID theaterId
    ) {
        return operatingHoursService.getTheaterOperatingHours(theaterId);
    }

    @HasManagerRole
    @CacheEvict(value = "operating_hours", key = "#theaterId")
    @PostMapping
    public Flux<OperatingHoursInfoResponseDto> saveTheaterOperatingHours(
            @PathVariable @NotNull(message = "The theater id must be valid.") UUID theaterId,
            @Valid @RequestBody List<OperatingHoursRequestDto> operatingHoursInfo
    ) {
        return operatingHoursService.saveTheaterOperatingHours(theaterId, operatingHoursInfo);
    }

    @HasManagerRole
    @CacheEvict(value = "operating_hours", key = "#theaterId")
    @PutMapping("/{operatingHoursId}")
    public Mono<ResponseEntity<OperatingHoursInfoResponseDto>> updateOperatingHours(
            @PathVariable @NotNull(message = "The theater id must be valid.") UUID theaterId,
            @PathVariable @NotNull(message = "The operating hours id must be valid.") UUID operatingHoursId,
            @Valid @RequestBody OperatingHoursRequestDto operatingHoursInfo
    ) {
        return operatingHoursService.updateOperatingHours(
                        theaterId,
                        operatingHoursId,
                        operatingHoursInfo
                )
                .map(ResponseEntity::ok);
    }

    @HasManagerRole
    @CacheEvict(value = "operating_hours", key = "#theaterId")
    @DeleteMapping("/{operatingHoursId}")
    public Mono<ResponseEntity<Void>> deleteOperatingHours(
            @PathVariable @NotNull(message = "The theater id must be valid.") UUID theaterId,
            @PathVariable @NotNull UUID operatingHoursId
    ) {
        return operatingHoursService.deleteOperatingHours(
                        theaterId,
                        operatingHoursId
                )
                .thenReturn(ResponseEntity.noContent().build());
    }

}
