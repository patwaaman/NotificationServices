package com.example.notificationService.ExceptionHandler;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.messaging.support.ErrorMessage;
import org.springframework.validation.FieldError;
import org.springframework.validation.ObjectError;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.client.RestClientException;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.concurrent.ExecutionException;

@RestControllerAdvice
public class ControllerExceptionHandler {
    @ExceptionHandler(Exception.class)

    public ResponseEntity<Object> handleAllExceptions(Exception ex, WebRequest request) {

        List<String> errors = new ArrayList<>();
        if(ex instanceof MethodArgumentNotValidException ){
            for (FieldError error : ((MethodArgumentNotValidException)ex).getBindingResult().getFieldErrors()) {
                errors.add(error.getField() + " -> " + error.getDefaultMessage());
            }

            for (ObjectError error : ((MethodArgumentNotValidException)ex).getBindingResult().getGlobalErrors()) {
                errors.add(error.getObjectName() + " -> " + error.getDefaultMessage());
            }
            ApiError apiError = ApiError.builder()
                    .message("Input Validation Error!!")
                    .status(HttpStatus.BAD_REQUEST)
                    .errors(errors)
                    .build();

            return new ResponseEntity<>(apiError, apiError.getStatus());
        }
        else if(ex instanceof NoSuchElementException || ex instanceof IllegalArgumentException){
            ApiError apiError = ApiError.builder().status(HttpStatus.BAD_REQUEST).build();
            return new ResponseEntity<>(apiError,apiError.getStatus());
        }
        else if(ex instanceof MethodArgumentTypeMismatchException){
            ApiError apiError = ApiError.builder()
                                .message(((MethodArgumentTypeMismatchException) ex).getMostSpecificCause().toString())
                                .status(HttpStatus.BAD_REQUEST)
                                .build();
            return new ResponseEntity<>(apiError,apiError.getStatus());
        }
        else if(ex instanceof HttpMessageNotReadableException){
            ApiError apiError = ApiError.builder()
                                .message(((HttpMessageNotReadableException) ex).getMostSpecificCause().toString())
                                .status(HttpStatus.BAD_REQUEST)
                                .build();
            return new ResponseEntity<>(apiError,apiError.getStatus());
        }
        else if(ex instanceof RestClientException){
            ApiError apiError = ApiError.builder()
                    .message(((RestClientException) ex).getMostSpecificCause().toString())
                    .status(HttpStatus.SERVICE_UNAVAILABLE)
                    .build();

            return new ResponseEntity<>(apiError, apiError.getStatus());

        }
        else if(ex instanceof ExecutionException){
            ApiError apiError = ApiError.builder()
                    .message(((ExecutionException) ex).toString())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

            return new ResponseEntity<>(apiError, apiError.getStatus());
        }
        else if(ex instanceof InterruptedException){
            ApiError apiError = ApiError.builder()
                    .message(((InterruptedException) ex).toString())
                    .status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .build();

            return new ResponseEntity<>(apiError, apiError.getStatus());
        }
        else{
            ApiError apiError = ApiError.builder().status(HttpStatus.INTERNAL_SERVER_ERROR).message(ex.getMessage() ).build();
            return new ResponseEntity<>(apiError,apiError.getStatus());
        }


    }

}
