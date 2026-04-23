package io.cinema.mstheaterseatmanagement.domain.dto.response;

import lombok.Builder;

import java.util.List;

@Builder
public record TheaterResponseDto(
        String theaterId,
        String name,
        String email,
        String phone,
        String location,
        List<OperatingHoursResponseDto> operatingHours
) {
}
