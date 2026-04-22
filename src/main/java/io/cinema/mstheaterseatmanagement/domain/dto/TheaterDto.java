package io.cinema.mstheaterseatmanagement.domain.dto;

import java.util.List;

public record TheaterDto(
        String name,
        String phone,
        String location,
        List<OperatingHoursDto> operatingHours
) {
}
