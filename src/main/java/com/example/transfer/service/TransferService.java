package com.example.transfer.service;

import com.example.transfer.transfer.model.Account;

public interface TransferService {
    String transfer(String idClient, double value, String idOrigin, String idDestination);

}
