package com.example.transfer.feign;

import com.example.transfer.exceptions.ResourceNotFoundException;
import feign.Response;
import feign.codec.ErrorDecoder;

public class CustomErrorDecoder implements ErrorDecoder {
    private final ErrorDecoder defaultErrorDecoder = new Default();

    @Override
    public Exception decode(String methodKey, Response response) {
        if (response.status() == 404) {
            switch (methodKey) {
                case "TransferenciaClient#getClientById(String)":
                    return new ResourceNotFoundException("Conta nao encontrada!");
                default:
                    return new ResourceNotFoundException("Resource not found: " + methodKey);
            }
        }
        return defaultErrorDecoder.decode(methodKey, response);
    }
}
