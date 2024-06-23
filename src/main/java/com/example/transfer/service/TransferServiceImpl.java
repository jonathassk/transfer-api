package com.example.transfer.service;

import com.example.transfer.transfer.model.Account;
import com.example.transfer.transfer.model.Transfer;

import java.util.Objects;

public class TransferServiceImpl implements TransferService{
    @Override
    public String transfer(String idClient, double value, String idOrigin, String idDestination) {
        Transfer transferData = new Transfer(idClient, value, new Account(idOrigin, idDestination));

        if (Objects.equals(transferData.account().idOrigin(), transferData.account().idDestination())) throw new IllegalArgumentException("Origin and destination accounts must be different");
        if (transferData.value() <= 0.00) throw new IllegalArgumentException("Value must be greater than zero");
        return null;
    }
}
