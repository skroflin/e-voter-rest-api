package com.skroflin.evoting_rest_api.exceptions;

import com.skroflin.evoting_rest_api.exceptions.election.*;
import com.skroflin.evoting_rest_api.exceptions.user.*;
import com.skroflin.evoting_rest_api.exceptions.user.verification.InvalidVerificationCodeException;
import com.skroflin.evoting_rest_api.exceptions.user.verification.VerificationCodeExpiredException;
import jakarta.persistence.EntityNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.LockedException;
import org.springframework.security.authorization.AuthorizationDeniedException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import javax.management.InstanceNotFoundException;
import java.nio.file.AccessDeniedException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;
import java.util.stream.Collectors;

@Slf4j
@ControllerAdvice
public class ApiExceptionHandler {

    private ResponseEntity<Object> buildResponse(String message, HttpStatus httpStatus) {
        ApiException apiException = new ApiException(
                message,
                httpStatus.value(),
                httpStatus,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler({ElectionNotStartedException.class, ElectionEndedException.class})
    public ResponseEntity<Object> handleElectionTimeException(RuntimeException e) {
        log.warn("Election time exception: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(Exception e) {
        log.error("Access denied exception: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AlreadyVotedException.class)
    public ResponseEntity<Object> handleAlreadyVotedException(AlreadyVotedException e) {
        log.warn("Voting attempt failed: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception e) {
        log.error("Unexpected error occurred: ", e);
        return buildResponse("Unexpected error has occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        String error = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        log.debug("Validation failer: {}", error);
        return buildResponse(error.isEmpty() ? "Validation error" : error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CandidateAlreadyExists.class)
    public ResponseEntity<Object> handleCandidateAlreadyExists(CandidateAlreadyExists e) {
        log.info("Candidate already exists: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException e) {
        log.warn("Bad credentials: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        log.warn("Illegal argument exception: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidPasswordException.class})
    public ResponseEntity<Object> handleInvalidPassword(InvalidPasswordException e) {
        log.warn("Invalid password: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            InstanceNotFoundException.class,
            EmptyResultDataAccessException.class,
            ResourceNotFoundException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<Object> handleResourceNotFound(Exception e) {
        log.warn("Resource not found: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InvalidEmailDomainException.class})
    public ResponseEntity<Object> handleInvalidEmailDomain(InvalidEmailDomainException e) {
        log.warn("Invalid email domain: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccountNotEnabledException.class, LockedException.class})
    public ResponseEntity<Object> handleAccountNotEnabled(AccountNotEnabledException e) {
        log.warn("Attempt to access non enabled account: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {UserAlreadyExistsException.class})
    public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException e) {
        log.warn("Conflict upon creation, user already exists: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {EmailAlreadyTakenException.class})
    public ResponseEntity<Object> handleEmailAlreadyTaken(EmailAlreadyTakenException e) {
        log.warn("Conflict upon creation, email already taken: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedExceptions(UnauthorizedException e) {
        log.warn("Unauthorized access: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {InvalidVerificationCodeException.class})
    public ResponseEntity<Object> handleInvalidVerificationCodeException(InvalidVerificationCodeException e) {
        log.warn("Invalid verification code: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {VerificationCodeExpiredException.class})
    public ResponseEntity<Object> handleVerificationCodeException(VerificationCodeExpiredException e) {
        log.info("Verification code expired: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.GONE);
    }

    @ExceptionHandler(value = {ElectionAlreadyExistsException.class})
    public ResponseEntity<Object> handleElectionAlreadyExistsException(ElectionAlreadyExistsException e) {
        log.warn("Attempt to duplicate election object: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {InvalidElectionException.class})
    public ResponseEntity<Object> handleInvalidElectionException(InvalidElectionException e) {
        log.warn("Incorrect election data: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ElectionNotOpenException.class)
    public ResponseEntity<Object> handleElectionNotOpen(ElectionNotOpenException e) {
        log.warn("Election not open: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ResponseEntity<Object> handleTokenAlreadyUsed(TokenAlreadyUsedException e) {
        log.warn("Token already used: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(VotingException.class)
    public ResponseEntity<Object> handleGeneralVotingException(VotingException e) {
        log.error("General voting error: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CriticalSignatureErrorException.class)
    public ResponseEntity<Object> handleCriticalSignatureErrorException(CriticalSignatureErrorException e) {
        log.error("Critical: error upon generating/validating signature: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(VerifySignatureException.class)
    public ResponseEntity<Object> handleSignatureVerificationException(VerifySignatureException e) {
        log.error("Critical error - error with cryptographic signature: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(EmailDeliveryFailedException.class)
    public ResponseEntity<Object> handleEmailDeliveryFailed(EmailDeliveryFailedException e) {
        log.error("Email delivery error: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(EmailServiceException.class)
    public ResponseEntity<Object> handleEmailServiceException(EmailServiceException e) {
        log.error("Email service unavailable: {}", e.getMessage());
        return buildResponse(e.getMessage(), HttpStatus.SERVICE_UNAVAILABLE);
    }
}
