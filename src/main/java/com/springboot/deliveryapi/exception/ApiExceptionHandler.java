package com.springboot.deliveryapi.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.method.annotation.MethodArgumentTypeMismatchException;

@RestControllerAdvice
public class ApiExceptionHandler {
    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({ApiBadRequestException.class})
    public ApiErrorResponse apiBadRequestHandling(ApiBadRequestException ex) {
        return new ApiErrorResponse(ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.FORBIDDEN)
    @ExceptionHandler({ApiForbiddenException.class})
    public ApiErrorResponse apiForbiddenHandling(ApiForbiddenException ex) {
        return new ApiErrorResponse(ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.NOT_FOUND)
    @ExceptionHandler({ApiResourceNotFoundException.class})
    public ApiErrorResponse apiResourceNotFoundHandling(ApiResourceNotFoundException ex) {
        return new ApiErrorResponse(ex.getMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentNotValidException.class})
    public ApiErrorResponse apiMethodArgumentNotValid(MethodArgumentNotValidException ex) {
        return new ApiErrorResponse(ex.getBindingResult().getFieldError().getDefaultMessage());
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({MethodArgumentTypeMismatchException.class})
    public ApiErrorResponse apiMethodArgumentTypeMismatch(MethodArgumentTypeMismatchException ex) {
        return new ApiErrorResponse("Data type of argument mismatch");
    }

    @ResponseStatus(value = HttpStatus.BAD_REQUEST)
    @ExceptionHandler({HttpMessageNotReadableException.class})
    public ApiErrorResponse apiHttpMessageNotReadableHandling(HttpMessageNotReadableException ex) {
        return new ApiErrorResponse("The format of request body is not correct");
    }

}
