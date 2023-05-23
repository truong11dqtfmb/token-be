package com.dqt.token.exceptions;

import com.dqt.token.dtos.response.ApiResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.HashMap;
import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {


    @ExceptionHandler(ResourceNotFoundException.class)
    public ResponseEntity<ApiResponse> handleResourceNotFoundException(ResourceNotFoundException ex) {
        String message = ex.getMessage();
        ApiResponse apiResponse = new ApiResponse(false, message);
        return new ResponseEntity<>(apiResponse, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String, Object>> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        Map<String, String> res = new HashMap<>();

        ex.getBindingResult().getAllErrors().forEach(
                (er) -> {
                    String fieldName = ((FieldError) er).getField();
                    String message = er.getDefaultMessage();
                    res.put(fieldName, message);
                }
        );

        Map<String, Object> map = new HashMap<>();
        map.put("data", res);
        map.put("message", "Invalid");
        map.put("status", false);

        return new ResponseEntity<>(map, HttpStatus.BAD_REQUEST);

    }


}
