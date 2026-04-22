package io.cinema.mstheaterseatmanagement.domain.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.relational.core.mapping.Table;

import java.util.UUID;

@Getter
@Setter
@Builder
@NoArgsConstructor
@Table("address")
@AllArgsConstructor
public class AddressEntity {
    @Id
    private UUID id;

    private String street;
    private String city;
    private String state;
    private String country;
    private String zip;

}

