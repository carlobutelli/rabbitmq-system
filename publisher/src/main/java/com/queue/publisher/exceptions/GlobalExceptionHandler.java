package com.queue.publisher.exceptions;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.context.request.WebRequest;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

/*
 * This class handles exception specific and global exception in one single place
 * */
@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<?> resourceNotFoundException(ResourceNotFoundException ex, WebRequest request) {
        String transactionId = request.getHeader("x-request-id");
        BaseResponseError errorDetails = new BaseResponseError(
                transactionId, new Date(), ex.getMessage(), request.getDescription(false)
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> globleExcpetionHandler(Exception ex, HttpServletRequest request) {
        String transactionId = request.getHeader("x-request-id");
        BaseResponseError errorDetails = new BaseResponseError(
                transactionId,
                new Date(),
                String.format("GlobalExcHandler: %s", ex.getMessage()),
                request.getRequestURI()
        );
        return new ResponseEntity<>(errorDetails, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
