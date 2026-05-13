package io.cinema.mstheaterseatmanagement;

import io.cinema.config.AuditingConfig;
import io.cinema.controller.ExceptionHandlers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;

@SpringBootApplication
@EnableReactiveMethodSecurity
@EnableCaching
@ComponentScan(basePackages = "io.cinema")
@Import({ExceptionHandlers.class, AuditingConfig.class})
public class MsTheaterSeatManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsTheaterSeatManagementApplication.class, args);
    }

}
