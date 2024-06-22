package com.example.transfer.service;

import com.example.transfer.transfer.model.Account;
import com.example.transfer.transfer.model.Transfer;

import java.util.UUID;

public class TransferServiceImpl implements TransferService{
    @Override
    public String transfer(String idClient, double value, String idOrigin, String idDestination) {
        Transfer transferData = new Transfer(idClient, value, new Account(idOrigin, idDestination));

        return null;
    }
}
