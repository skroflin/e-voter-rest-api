package com.skroflin.evoting_rest_api.exceptions;

import com.skroflin.evoting_rest_api.exceptions.user.*;
import com.skroflin.evoting_rest_api.exceptions.user.verification.InvalidVerificationCodeException;
import com.skroflin.evoting_rest_api.exceptions.user.verification.VerificationCodeExpiredException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.management.InstanceNotFoundException;
import java.time.ZoneId;
import java.time.ZonedDateTime;

@ControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException e) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        ApiException apiException = new ApiException(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = {InvalidPasswordException.class})
    public ResponseEntity<Object> handleInvalidPassword(InvalidPasswordException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = {InstanceNotFoundException.class, EmptyResultDataAccessException.class, ResourceNotFoundException.class})
    public ResponseEntity<Object> handleResourceNotFound(ResourceNotFoundException e) {
        HttpStatus httpStatus = HttpStatus.NOT_FOUND;
        ApiException apiException = new ApiException(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = {InvalidEmailDomainException.class})
    public ResponseEntity<Object> handleInvalidEmailDomain(InvalidEmailDomainException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = {AccountNotEnabledException.class, LockedException.class})
    public ResponseEntity<Object> handleAccountNotEnabled(AccountNotEnabledException e) {
        HttpStatus httpStatus = HttpStatus.FORBIDDEN;
        ApiException apiException = new ApiException(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = {UserAlreadyExistsException.class})
    public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException e) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        ApiException apiException = new ApiException(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = {EmailAlreadyTakenException.class})
    public ResponseEntity<Object> handleEmailAlreadyTaken(EmailAlreadyTakenException e) {
        HttpStatus httpStatus = HttpStatus.CONFLICT;
        ApiException apiException = new ApiException(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedExceptions(UnauthorizedException e) {
        HttpStatus httpStatus = HttpStatus.UNAUTHORIZED;
        ApiException apiException = new ApiException(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, httpStatus);
    }

    // TODO: Add other exception handlers for handling user registration

    // TODO: Add exception handlers for verification code exceptions

    @ExceptionHandler(value = {InvalidVerificationCodeException.class})
    public ResponseEntity<Object> handleInvalidVerificationCodeException(InvalidVerificationCodeException e) {
        HttpStatus httpStatus = HttpStatus.BAD_REQUEST;
        ApiException apiException = new ApiException(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler(value = {VerificationCodeExpiredException.class})
    public ResponseEntity<Object> handleVerificationCodeException(VerificationCodeExpiredException e) {
        HttpStatus httpStatus = HttpStatus.GONE;
        ApiException apiException = new ApiException(e.getMessage(), httpStatus, ZonedDateTime.now(ZoneId.of("Z")));
        return new ResponseEntity<>(apiException, httpStatus);
    }
}
