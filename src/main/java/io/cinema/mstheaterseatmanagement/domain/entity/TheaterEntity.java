package io.cinema.mstheaterseatmanagement.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Column;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@Builder
@Table("theaters")
@NoArgsConstructor
@AllArgsConstructor
public class TheaterEntity extends AuditableEntity {
    @Id
    private UUID id;

    private String name;
    private String phone;

    @Column("address_id")
    private UUID addressId;

}
