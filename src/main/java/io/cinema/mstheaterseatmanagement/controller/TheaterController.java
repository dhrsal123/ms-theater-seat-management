package io.cinema.mstheaterseatmanagement.controller;

import io.cinema.mstheaterseatmanagement.domain.dto.TheaterDto;
import io.cinema.mstheaterseatmanagement.service.TheaterService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.util.UUID;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/v1/theater")
public class TheaterController {

    private final TheaterService theaterService;

    //    @Cacheable(cacheNames = CachingConfig.THEATERS_CACHE)
    @GetMapping(params = {"page", "size"})
    public ResponseEntity<Flux<TheaterDto>> getAllTheaters(
            @RequestParam("page") int page,
            @RequestParam("size") int size
    ) {
        return ResponseEntity.ok(theaterService.getAllTheaters(page, size));
    }

    @GetMapping("/{theaterId}")
    public ResponseEntity<Mono<TheaterDto>> getTheaterById(@PathVariable UUID theaterId) {
        return ResponseEntity.ok(theaterService.getTheaterById(theaterId));
    }

}
