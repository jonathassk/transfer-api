package com.example.transfer.controller;

import org.springframework.web.bind.annotation.RestController;

@RestController
public class TransferControllerImpl implements TransferController {
    @Override
    public String transfer(String idClient, double value, String idOrigin, String idDestination) {
        return null;
    }
}
