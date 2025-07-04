package com.urlshortner.urlshortner.exception;

import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final Logger logger = LoggerFactory.getLogger(GlobalExceptionHandler.class);

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ApiError> handleRunTime(RuntimeException ex, HttpServletRequest request){
        logger.error("RuntimeException at {}: {}",request.getRequestURI(),ex.getMessage());
        ApiError error = new ApiError(HttpStatus.INTERNAL_SERVER_ERROR.value(),"Internal Server Error", ex.getMessage(), request.getRequestURI());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ApiError> handleBadRequest(IllegalArgumentException ex,HttpServletRequest request){
        logger.warn("Bad request at {}: {}", request.getRequestURI(),ex.getMessage());
        ApiError error = new ApiError(HttpStatus.BAD_REQUEST.value(), "Bad Request",ex.getMessage(),request.getRequestURI());
        return ResponseEntity.badRequest().body(error);
    }
}
