package com.lupa.utils.exception;

import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class BusinessException extends RuntimeException implements ExceptionHandler {

    private ExceptionHandler exceptionHandler;

    public BusinessException(){}

    public BusinessException(String message){
        super(message);
    }

    public BusinessException(ExceptionHandler exceptionHandler) {
        super();
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public long getCode() {
        return this.exceptionHandler.getCode();
    }

    @Override
    public String getMessage(){
        return this.exceptionHandler.getMessage();
    }
}
