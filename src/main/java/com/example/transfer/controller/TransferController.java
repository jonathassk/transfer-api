package com.example.transfer.controller;

import com.example.transfer.transfer.model.Transfer;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.transaction.Transactional;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;
import java.util.Optional;
import java.util.stream.Stream;

@RestController
public class TransferController {

    @PostMapping("/transferencia")
    @Transactional
    public String transfer(String idClient, double value, String idOrigin, String idDestination, HttpServletResponse response) {
        verifyEmptyOrNullFields(idClient, value, idOrigin, idDestination);

        return ResponseEntity.status(201).body(idClient).toString();
    }

    private void verifyEmptyOrNullFields(String idClient, double value, String idOrigin, String idDestination) {
        if (Stream.of(idClient, idOrigin, idDestination).anyMatch(Objects::isNull)) {
            throw new IllegalArgumentException("All fields must be filled");
        }
    }
}
