package com.fmi.reviews.web.rest;

import com.fmi.reviews.exception.InvalidEntityDataException;
import com.fmi.reviews.exception.UnautorizedRequestException;
import com.fmi.reviews.exception.UnexistingEntityException;
import com.fmi.reviews.model.ErrorResponse;
import com.fmi.reviews.web.mvc.ErrorHandlerMvcControllerAdvice;
import io.jsonwebtoken.JwtException;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice(basePackageClasses = ErrorHandlerMvcControllerAdvice.class)
public class ErrorHandlerRestControllerAdvice {

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleInvalidDataException(InvalidEntityDataException ex) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage(), ex.getViolations()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnexistingEntityException(UnexistingEntityException ex) {
        return ResponseEntity.badRequest().body(
                new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage()));
    }


    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleHttpMessageNotReadable(HttpMessageNotReadableException ex) {
        return ResponseEntity.badRequest()
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), ex.getMessage()));
    }

    @ExceptionHandler({JwtException.class, AuthenticationException.class})
    public ResponseEntity<ErrorResponse> handleAuthenticationException(RuntimeException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(new ErrorResponse(HttpStatus.UNAUTHORIZED.value(), ex.getMessage()));
    }

    @ExceptionHandler
    public ResponseEntity<ErrorResponse> handleUnauthorizedException(UnautorizedRequestException ex){
        return ResponseEntity.status(HttpStatus.FORBIDDEN)
                .body(new ErrorResponse(HttpStatus.FORBIDDEN.value(), ex.getMessage()));
    }
}
