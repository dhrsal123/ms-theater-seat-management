package io.cinema.mstheaterseatmanagement.controller;

import io.cinema.domain.annotations.HasManagerRole;
import io.cinema.mstheaterseatmanagement.domain.dto.request.TheaterRequestDto;
import io.cinema.mstheaterseatmanagement.domain.dto.response.TheaterResponseDto;
import io.cinema.mstheaterseatmanagement.service.TheaterService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@Validated
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/theater")
public class TheaterController {

    private final TheaterService theaterService;

    //    @Cacheable(cacheNames = CachingConfig.THEATERS_CACHE)
    @GetMapping(params = {"page", "size"})
    public ResponseEntity<Flux<TheaterResponseDto>> getAllTheaters(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        return ResponseEntity.ok(theaterService.getAllTheaters(page, size));
    }

    @GetMapping("/{theaterId}")
    public Mono<ResponseEntity<TheaterResponseDto>> getTheaterById(@PathVariable @NotNull UUID theaterId) {
        return theaterService.getTheaterById(theaterId)
                .map(ResponseEntity::ok)
                .switchIfEmpty(Mono.just(ResponseEntity.notFound().build()));
    }

    @HasManagerRole
    @PostMapping
    public Mono<ResponseEntity<TheaterResponseDto>> createTheater(@RequestBody @Valid TheaterRequestDto theater) {
        return theaterService.createTheater(theater)
                .map(ResponseEntity::ok);
    }

    @HasManagerRole
    @DeleteMapping("/{theaterId}")
    public Mono<ResponseEntity<Void>> deleteTheater(@PathVariable @NotNull UUID theaterId) {
        return theaterService.deleteTheater(theaterId)
                .thenReturn(ResponseEntity.noContent().build());
    }

}
