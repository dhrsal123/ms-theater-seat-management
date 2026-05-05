package io.cinema.mstheaterseatmanagement.domain.entity;

import io.cinema.domain.entity.AuditableEntity;
import io.cinema.mstheaterseatmanagement.domain.enumerated.SeatStatus;
import io.cinema.mstheaterseatmanagement.domain.enumerated.SeatTypes;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@Table("seat")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class SeatEntity extends AuditableEntity {
    @Id
    private UUID id;

    private Double priceIncrement;

    private Integer rowNumber;
    private Integer colNumber;

    private SeatStatus seatStatus;
    private SeatTypes seatType;

    private UUID roomId;

}
