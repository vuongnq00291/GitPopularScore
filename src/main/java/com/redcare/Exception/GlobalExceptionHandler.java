package com.redcare.Exception;

import com.redcare.config.MessageConfig;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MissingServletRequestParameterException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    private final MessageConfig message;

    public GlobalExceptionHandler(MessageConfig message) {
        this.message = message;
    }

    @ExceptionHandler(MissingServletRequestParameterException.class)
    public ResponseEntity<Map<String, String>> handleMissingParams(MissingServletRequestParameterException ex) {
        String paramName = ex.getParameterName();
        Map<String, String> errorResponse = new HashMap<>();
        String errorMessage = String.format(message.getMissingParameter(), paramName);
        errorResponse.put("error", errorMessage);
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(InvalidDateFormatException.class)
    public ResponseEntity<Map<String, String>> handleInvalidDateFormat(InvalidDateFormatException ex) {
        Map<String, String> errorResponse = new HashMap<>();
        errorResponse.put("error", ex.getMessage());
        return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
    }
}
