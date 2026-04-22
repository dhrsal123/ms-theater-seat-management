package io.cinema.mstheaterseatmanagement.mapper;

import io.cinema.mstheaterseatmanagement.domain.dto.OperatingHoursDto;
import io.cinema.mstheaterseatmanagement.domain.dto.TheaterDto;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterRowProjection;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TheaterProjectionMapper {

    public static TheaterDto toTheaterDto(List<TheaterRowProjection> projections, UUID theaterId) {
        if (projections.isEmpty()) {
            return null;
        }
        var base = projections.getFirst();
        var hoursList = projections.stream()
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
                theaterId.toString(),
                base.name(),
                base.phone(),
                address,
                hoursList
        );
    }
}
