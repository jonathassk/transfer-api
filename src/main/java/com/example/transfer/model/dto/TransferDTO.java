package com.example.transfer.model.dto;

import com.example.transfer.enums.Status;


public class TransferDTO {

    private Long id;
    private String idCliente;
    private Status status;
    private double valor;

    public TransferDTO (String idCliente, Status status, double valor) {
        this.idCliente = idCliente;
        this.status = status;
        this.valor = valor;
    }
}
