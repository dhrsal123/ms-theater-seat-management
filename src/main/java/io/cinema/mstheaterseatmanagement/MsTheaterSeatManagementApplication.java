package io.cinema.mstheaterseatmanagement;

import io.cinema.config.AuditingConfig;
import io.cinema.controller.ExceptionHandlers;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Import;

@SpringBootApplication
@Import({ExceptionHandlers.class, AuditingConfig.class})
public class MsTheaterSeatManagementApplication {

    public static void main(String[] args) {
        SpringApplication.run(MsTheaterSeatManagementApplication.class, args);
    }

}
