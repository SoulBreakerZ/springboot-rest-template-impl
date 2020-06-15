package com.arkontec.core.rest.application.exception;

import org.springframework.http.HttpStatus;

public class RestCoreException extends RuntimeException {

    private HttpStatus statusCode;
    private String error;
    private ErrorCode errorCode;

    public RestCoreException(HttpStatus statusCode,String error){
        super(error);
        this.statusCode = statusCode;
        this.error = error;
        if(statusCode.is4xxClientError()){
            errorCode = ErrorCode.CLIENT_EXCEPTION;
        }else if(statusCode.is5xxServerError()){
            errorCode = ErrorCode.SERVER_EXCEPTION;
        }
    }

    public RestCoreException(ErrorCode errorCode,String error){
        super(error);
        this.errorCode = errorCode;
        this.error = error;
    }

    public ErrorCode getErrorCode() {
        return errorCode;
    }

    public void setErrorCode(ErrorCode errorCode) {
        this.errorCode = errorCode;
    }

    public HttpStatus getStatusCode() {
        return statusCode;
    }

    public void setStatusCode(HttpStatus statusCode) {
        this.statusCode = statusCode;
    }

    public String getError() {
        return error;
    }

    public void setError(String error) {
        this.error = error;
    }
}
