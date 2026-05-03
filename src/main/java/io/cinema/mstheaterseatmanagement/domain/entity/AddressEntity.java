package io.cinema.mstheaterseatmanagement.domain.entity;

import io.cinema.domain.entity.AuditableEntity;
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
@Table("address")
@NoArgsConstructor
@AllArgsConstructor
@SuperBuilder(toBuilder = true)
public class AddressEntity extends AuditableEntity {
    @Id
    private UUID id;

    private String street;
    private String city;
    private String state;
    private String country;
    private String zip;

}

