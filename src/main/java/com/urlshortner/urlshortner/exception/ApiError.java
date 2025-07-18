package com.urlshortner.urlshortner.exception;

import lombok.Data;

import java.time.LocalDateTime;

@Data
public class ApiError {

    private LocalDateTime timeStamp;
    private int status;
    private String error;
    private String message;
    private String path;

    public ApiError(int status,String error,String message,String path){
        this.status=status;
        this.error=error;
        this.message=message;
        this.path=path;
    }

}
