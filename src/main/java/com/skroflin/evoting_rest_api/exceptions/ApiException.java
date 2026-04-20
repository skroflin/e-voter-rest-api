package com.skroflin.evoting_rest_api.exceptions;

import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;

import java.time.ZonedDateTime;

public record ApiException(
        String message,
        int status,
        HttpStatus httpStatus,
        ZonedDateTime zonedDateTime
) { }
