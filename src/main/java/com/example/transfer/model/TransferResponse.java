package com.example.transfer.model;

import org.springframework.http.HttpStatus;

public record TransferResponse (HttpStatus status, String message) {
}
