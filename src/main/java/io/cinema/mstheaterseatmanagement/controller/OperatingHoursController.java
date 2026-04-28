package io.cinema.mstheaterseatmanagement.controller;


import io.cinema.domain.annotations.HasEmployeeRole;
import io.cinema.domain.annotations.HasManagerRole;
import io.cinema.mstheaterseatmanagement.domain.dto.request.OperatingHoursInfoRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.request.OperatingHoursRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.OperatingHoursInfoResponseDto;
import io.cinema.mstheaterseatmanagement.service.OperatingHoursService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1")
public class OperatingHoursController {
    private final OperatingHoursService operatingHoursService;

    @HasManagerRole
    @GetMapping("/theater/{theaterId}/operating-hours")
    public ResponseEntity<Flux<OperatingHoursInfoResponseDto>> getTheaterOperatingHours(
            @PathVariable @NotNull UUID theaterId
    ) {
        return ResponseEntity.ok(operatingHoursService.getTheaterOperatingHours(theaterId));
    }

    @HasManagerRole
    @PostMapping("/theater/{theaterId}/operating-hours")
    public Mono<ResponseEntity<OperatingHoursInfoResponseDto>> saveTheaterOperatingHours(
            @PathVariable @NotNull UUID theaterId,
            @Valid @RequestBody OperatingHoursRequestDto operatingHoursInfo
    ) {
        return operatingHoursService.saveTheaterOperatingHours(theaterId, operatingHoursInfo)
                .map(ResponseEntity::ok);
    }

    @HasManagerRole
    @PutMapping("/operatingHours")
    public Mono<ResponseEntity<OperatingHoursInfoResponseDto>> updateOperatingHours(
            @Valid @RequestBody OperatingHoursInfoRequestDto operatingHoursInfo
    ) {
        return operatingHoursService.updateOperatingHours(operatingHoursInfo)
                .map(ResponseEntity::ok);
    }

    @HasManagerRole
    @DeleteMapping("/operatingHours/{operatingHoursId}")
    public Mono<ResponseEntity<Void>> deleteOperatingHours(
            @PathVariable @NotNull UUID operatingHoursId
    ) {
        return operatingHoursService.deleteOperatingHours(operatingHoursId)
                .thenReturn(ResponseEntity.noContent().build());
    }

}
