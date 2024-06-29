package com.example.transfer.service;

import com.example.transfer.model.Transfer;
import com.example.transfer.model.TransferResponse;

public interface TransferService {
    TransferResponse transfer(Transfer body);

}
