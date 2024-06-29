package com.example.transfer.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

public record Transfer (@NotBlank(message = "idCliente cannot be null or empty") String idCliente, @NotNull(message = "valor cannot be null") double valor, Account conta) {
}
