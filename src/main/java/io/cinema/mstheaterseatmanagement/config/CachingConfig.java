package io.cinema.mstheaterseatmanagement.config;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Configuration;

@Configuration
@EnableCaching
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class CachingConfig {
    public static final String THEATERS_CACHE = "THEATERS";
    public static final String SEATS_CACHE = "SEATS";

}
