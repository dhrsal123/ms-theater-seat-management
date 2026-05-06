package io.cinema.mstheaterseatmanagement.repository;

import io.cinema.mstheaterseatmanagement.domain.entity.OperatingHoursEntity;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.r2dbc.test.autoconfigure.DataR2dbcTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;
import reactor.test.StepVerifier;

import java.time.DayOfWeek;
import java.time.LocalTime;
import java.util.List;
import java.util.UUID;

@DataR2dbcTest
@Testcontainers
class OperatingHoursRepositoryTest {

    @Container
    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>("postgres:15-alpine");

    @DynamicPropertySource
    static void registerDynamicProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.r2dbc.url", () -> "r2dbc:postgresql://"
                + postgres.getHost() + ":" + postgres.getFirstMappedPort()
                + "/" + postgres.getDatabaseName());
        registry.add("spring.r2dbc.username", postgres::getUsername);
        registry.add("spring.r2dbc.password", postgres::getPassword);

        registry.add("spring.flyway.url", postgres::getJdbcUrl);
        registry.add("spring.flyway.user", postgres::getUsername);
        registry.add("spring.flyway.password", postgres::getPassword);
    }

    @Autowired
    private OperatingHoursRepository operatingHoursRepository;

    @AfterEach
    void tearDown() {
        operatingHoursRepository.deleteAll().block();
    }

    @Test
    void shouldFindOperatingHoursByTheaterId() {
        UUID targetTheaterId = UUID.randomUUID();
        UUID otherTheaterId = UUID.randomUUID();

        OperatingHoursEntity targetEntity1 = new OperatingHoursEntity(
                UUID.randomUUID(), DayOfWeek.MONDAY, LocalTime.NOON, LocalTime.MIDNIGHT, targetTheaterId);
        OperatingHoursEntity targetEntity2 = new OperatingHoursEntity(
                UUID.randomUUID(), DayOfWeek.TUESDAY, LocalTime.NOON, LocalTime.MIDNIGHT, targetTheaterId);
        OperatingHoursEntity ignoredEntity = new OperatingHoursEntity(
                UUID.randomUUID(), DayOfWeek.WEDNESDAY, LocalTime.NOON, LocalTime.MIDNIGHT, otherTheaterId);

        operatingHoursRepository.saveAll(List.of(targetEntity1, targetEntity2, ignoredEntity)).blockLast();

        var result = operatingHoursRepository.findOperatingHoursByTheaterId(targetTheaterId);

        StepVerifier.create(result)
                .expectNextMatches(entity -> entity.getTheaterId().equals(targetTheaterId))
                .expectNextMatches(entity -> entity.getTheaterId().equals(targetTheaterId))
                .verifyComplete();
    }

    @Test
    void shouldDeleteAllByTheaterId() {
        UUID targetTheaterId = UUID.randomUUID();
        UUID survivorTheaterId = UUID.randomUUID();

        OperatingHoursEntity entityToDelete = new OperatingHoursEntity(
                UUID.randomUUID(), DayOfWeek.MONDAY, LocalTime.NOON, LocalTime.MIDNIGHT, targetTheaterId);
        OperatingHoursEntity entityToSurvive = new OperatingHoursEntity(
                UUID.randomUUID(), DayOfWeek.WEDNESDAY, LocalTime.NOON, LocalTime.MIDNIGHT, survivorTheaterId);

        operatingHoursRepository.saveAll(List.of(entityToDelete, entityToSurvive)).blockLast();

        var deleteOperation = operatingHoursRepository.deleteAllByTheaterId(targetTheaterId);

        StepVerifier.create(deleteOperation).verifyComplete();

        StepVerifier.create(operatingHoursRepository.findAll())
                .expectNextMatches(entity -> entity.getTheaterId().equals(survivorTheaterId))
                .verifyComplete();
    }
}