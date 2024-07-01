package com.example.transfer.service;

import com.example.transfer.exceptions.ResourceNotFoundException;
import com.example.transfer.feign.CustomErrorDecoder;
import com.example.transfer.feign.TransferenciaClient;
import com.example.transfer.model.*;
import feign.Response;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class TransferServiceImplTest {
    @Mock
    private TransferenciaClient transferenciaClient;

    @Mock
    private CircuitBreakerRegistry circuitBreakerRegistry;
    @Mock
    private RetryRegistry retryRegistry;

    @Mock
    private CircuitBreaker circuitBreaker;
    @Mock
    private Retry retry;

    @Mock
    private CustomErrorDecoder customErrorDecoder;

    @InjectMocks
    private TransferServiceImpl transferService;
    @BeforeEach
    void setUp() {
        when(circuitBreakerRegistry.circuitBreaker(anyString())).thenReturn(circuitBreaker);
        when(retryRegistry.retry(anyString(), anyString())).thenReturn(retry);
    }

    @Test
    void testTransferSuccessQuantityInvocationMethods() throws Throwable {
        Transfer transfer = mockTransferCorreto();
        when(transferenciaClient.getClientById(anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(transferenciaClient.getContaByIdOrigem(anyString())).thenReturn(new ResponseEntity<ContaResponse>((new ContaResponse("id", 5000.00, true, 500.0)), HttpStatus.OK));
        when(transferenciaClient.enviarNotificacao(any(Transfer.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        TransferResponse response = transferService.transfer(transfer);

        assertEquals(HttpStatus.CREATED, response.status());
        assertNotNull(response.message());
        verify(transferenciaClient, times(1)).enviarNotificacao(transfer);
        verify(transferenciaClient, times(1)).getContaByIdOrigem(transfer.conta().idOrigem());
    }

    @Test
    void testTransferSuccess() throws Throwable {
        Transfer transfer = mockTransferCorreto();
        when(transferenciaClient.getClientById(anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(transferenciaClient.getContaByIdOrigem(anyString())).thenReturn(new ResponseEntity<ContaResponse>((new ContaResponse("id", 5000.00, true, 500.0)), HttpStatus.OK));
        when(transferenciaClient.enviarNotificacao(any(Transfer.class))).thenReturn(new ResponseEntity<>(HttpStatus.OK));

        TransferResponse response = transferService.transfer(transfer);

        assertEquals(HttpStatus.CREATED, response.status());
        assertNotNull(response.message());
    }

    @Test
    void testTransferFailedNotAtiveUser() {
        Transfer transfer = mockTransferCorreto();
        when(transferenciaClient.getClientById(anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(transferenciaClient.getContaByIdOrigem(anyString())).thenReturn(new ResponseEntity<ContaResponse>((new ContaResponse("id", 5000.00, false, 500.0)), HttpStatus.OK));

        TransferResponse response = transferService.transfer(transfer);

        assertEquals(HttpStatus.BAD_REQUEST, response.status());
        assertEquals("Conta de origem não se encontra ativa", response.message());
        verify(transferenciaClient, never()).enviarNotificacao(any());
    }

    @Test
    void testTransferFailNotEnoughSaldo() {
        Transfer transfer = mockTransferCorreto();
        when(transferenciaClient.getClientById(anyString())).thenReturn(new ResponseEntity<>(HttpStatus.OK));
        when(transferenciaClient.getContaByIdOrigem(anyString())).thenReturn(new ResponseEntity<ContaResponse>((new ContaResponse("id", 10.00, true, 500.0)), HttpStatus.OK));

        TransferResponse response = transferService.transfer(transfer);

        assertEquals(HttpStatus.BAD_REQUEST, response.status());
        assertEquals(response.message(), "Saldo insuficiente");
        verify(transferenciaClient, never()).enviarNotificacao(any());
    }

    @Test
    void testTransferFailedSameAccount() {
        Transfer transfer = new Transfer("2ceb26e9-7b5c-417e-bf75-ffaa66e3a76f", 100.0, new Account("d0d32142-74b7-4aca-9c68-838aeacef96b", "d0d32142-74b7-4aca-9c68-838aeacef96b"));

        TransferResponse response = transferService.transfer(transfer);

        assertEquals(HttpStatus.BAD_REQUEST, response.status());
        assertEquals("Conta de origem e destino não podem ser iguais", response.message());
        verify(transferenciaClient, never()).enviarNotificacao(any());
    }

    private Transfer mockTransferCorreto() {
        Account conta = new Account("d0d32142-74b7-4aca-9c68-838aeacef96b", "41313d7b-bd75-4c75-9dea-1f4be434007f");
        return new Transfer("2ceb26e9-7b5c-417e-bf75-ffaa66e3a76f", 100.0, conta);
    }
}

