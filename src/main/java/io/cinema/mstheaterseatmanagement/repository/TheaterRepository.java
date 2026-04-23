package io.cinema.mstheaterseatmanagement.repository;

import io.cinema.mstheaterseatmanagement.domain.entity.TheaterEntity;
import io.cinema.mstheaterseatmanagement.domain.entity.TheaterRowProjection;
import org.springframework.data.r2dbc.repository.Query;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import reactor.core.publisher.Flux;

import java.util.UUID;

public interface TheaterRepository extends ReactiveCrudRepository<TheaterEntity, UUID> {

    @Query("""
                SELECT 
                    t.id as theater_id,
                    t.name, t.email, t.phone,
                    a.street, a.city, a.state, a.country, a.zip,
                    oh.day_of_week as day_of_week, 
                    oh.start_time as start_time, 
                    oh.end_time as end_time 
                FROM (
                    SELECT * FROM theaters 
                    ORDER BY name 
                    LIMIT :size OFFSET :offset
                ) t
                JOIN address a ON t.address_id = a.id
                LEFT JOIN operating_hours oh ON t.id = oh.theater_id
            """)
    Flux<TheaterRowProjection> findAllTheaterDetails(int size, long offset);

    @Query("""
                SELECT 
                    t.id as theater_id,
                    t.name, t.email, t.phone,
                    a.street, a.city, a.state, a.zip,
                    oh.day_of_week as day_of_week, 
                    oh.start_time as start_time, 
                    oh.end_time as end_time 
                FROM (
                    SELECT * FROM theaters 
                    WHERE theaters.id = :id
                ) t
                JOIN address a ON t.address_id = a.id
                LEFT JOIN operating_hours oh ON t.id = oh.theater_id
            """)
    Flux<TheaterRowProjection> findTheaterDetailsById(UUID id);

}
