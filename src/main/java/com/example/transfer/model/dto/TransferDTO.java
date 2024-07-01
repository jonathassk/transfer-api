package com.example.transfer.model.dto;

import com.example.transfer.enums.Status;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;


@Entity
public class TransferDTO {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
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
