package com.example.transfer.model;

import jakarta.validation.constraints.NotBlank;

public record Account(@NotBlank(message = "idOrigem cannot be empty") String idOrigem, @NotBlank(message = "idDestino cannot be empty") String idDestino) {
}
