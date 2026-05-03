package io.cinema.mstheaterseatmanagement.domain.entity;

import io.cinema.domain.entity.AuditableEntity;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@Table("theater")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class TheaterEntity extends AuditableEntity {
    @Id
    private UUID id;

    private String name;
    private String email;
    private String phone;

    @Column("address_id")
    private UUID addressId;

}
