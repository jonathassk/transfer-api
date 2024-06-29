package com.example.transfer.exceptions;

import org.springframework.http.HttpStatus;

public class TransferException extends RuntimeException {

    private final HttpStatus status;
    public TransferException(String message, HttpStatus status) {
        super(message);
        this.status = status;
    }

    public HttpStatus getStatus() {
        return status;
    }
}
