package com.skroflin.evoting_rest_api.exceptions;

import com.skroflin.evoting_rest_api.exceptions.election.CandidateAlreadyExists;
import com.skroflin.evoting_rest_api.exceptions.election.ElectionAlreadyExistsException;
import com.skroflin.evoting_rest_api.exceptions.election.ElectionNotOpenException;
import com.skroflin.evoting_rest_api.exceptions.election.InvalidElectionException;
import com.skroflin.evoting_rest_api.exceptions.user.*;
import com.skroflin.evoting_rest_api.exceptions.user.verification.InvalidVerificationCodeException;
import com.skroflin.evoting_rest_api.exceptions.user.verification.VerificationCodeExpiredException;
import jakarta.persistence.EntityNotFoundException;
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

@ControllerAdvice
public class ApiExceptionHandler {

    private ResponseEntity<Object> buildResponse(String message, HttpStatus httpStatus) {
        ApiException apiException = new ApiException(
                message,
                httpStatus,
                ZonedDateTime.now(ZoneId.of("Z"))
        );
        return new ResponseEntity<>(apiException, httpStatus);
    }

    @ExceptionHandler({AccessDeniedException.class, AuthorizationDeniedException.class})
    public ResponseEntity<Object> handleAccessDeniedException(Exception e) {
        return buildResponse(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(AlreadyVotedException.class)
    public ResponseEntity<Object> handleAlreadyVotedException(AlreadyVotedException e) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<Object> handleGlobalException(Exception e) {
        return buildResponse("Unexpected error has occurred", HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Object> handleValidationException(MethodArgumentNotValidException e) {
        String error = e.getBindingResult()
                .getFieldErrors()
                .stream()
                .map(fieldError -> fieldError.getField() + ": " + fieldError.getDefaultMessage())
                .collect(Collectors.joining(", "));

        if (error.isEmpty()) {
            error = "Validation error";
        }

        return buildResponse(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CandidateAlreadyExists.class)
    public ResponseEntity<Object> handleCandidateAlreadyExists(CandidateAlreadyExists e) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Object> handleBadCredentials(BadCredentialsException e) {
        return buildResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<Object> handleIllegalArgument(IllegalArgumentException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {InvalidPasswordException.class})
    public ResponseEntity<Object> handleInvalidPassword(InvalidPasswordException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {
            InstanceNotFoundException.class,
            EmptyResultDataAccessException.class,
            ResourceNotFoundException.class,
            EntityNotFoundException.class
    })
    public ResponseEntity<Object> handleResourceNotFound(Exception e) {
        return buildResponse(e.getMessage(), HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(value = {InvalidEmailDomainException.class})
    public ResponseEntity<Object> handleInvalidEmailDomain(InvalidEmailDomainException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {AccountNotEnabledException.class, LockedException.class})
    public ResponseEntity<Object> handleAccountNotEnabled(AccountNotEnabledException e) {
        return buildResponse(e.getMessage(), HttpStatus.FORBIDDEN);
    }

    @ExceptionHandler(value = {UserAlreadyExistsException.class})
    public ResponseEntity<Object> handleUserAlreadyExists(UserAlreadyExistsException e) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {EmailAlreadyTakenException.class})
    public ResponseEntity<Object> handleEmailAlreadyTaken(EmailAlreadyTakenException e) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {UnauthorizedException.class})
    public ResponseEntity<Object> handleUnauthorizedExceptions(UnauthorizedException e) {
        return buildResponse(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(value = {InvalidVerificationCodeException.class})
    public ResponseEntity<Object> handleInvalidVerificationCodeException(InvalidVerificationCodeException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(value = {VerificationCodeExpiredException.class})
    public ResponseEntity<Object> handleVerificationCodeException(VerificationCodeExpiredException e) {
        return buildResponse(e.getMessage(), HttpStatus.GONE);
    }

    @ExceptionHandler(value = {ElectionAlreadyExistsException.class})
    public ResponseEntity<Object> handleElectionAlreadyExistsException(ElectionAlreadyExistsException e) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(value = {InvalidElectionException.class})
    public ResponseEntity<Object> handleInvalidElectionException(InvalidElectionException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(ElectionNotOpenException.class)
    public ResponseEntity<Object> handleElectionNotOpen(ElectionNotOpenException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(TokenAlreadyUsedException.class)
    public ResponseEntity<Object> handleTokenAlreadyUsed(TokenAlreadyUsedException e) {
        return buildResponse(e.getMessage(), HttpStatus.CONFLICT);
    }

    @ExceptionHandler(VotingException.class)
    public ResponseEntity<Object> handleGeneralVotingException(VotingException e) {
        return buildResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(CriticalSignatureErrorException.class)
    public ResponseEntity<Object> handleCriticalSignatureErrorException(CriticalSignatureErrorException e) {
        return buildResponse(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(VerifySignatureException.class)
    public ResponseEntity<Object> handleSignatureVerificationException(VerifySignatureException e) {
        return buildResponse(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}
