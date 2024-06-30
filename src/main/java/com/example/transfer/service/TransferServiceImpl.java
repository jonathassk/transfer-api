package com.example.transfer.service;

import com.example.transfer.exceptions.TransferException;
import com.example.transfer.feign.TransferenciaClient;
import com.example.transfer.model.ClienteResponse;
import com.example.transfer.model.ContaResponse;
import com.example.transfer.model.Transfer;
import com.example.transfer.model.TransferResponse;
import feign.Response;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransferServiceImpl implements TransferService{

    private final TransferenciaClient transferenciaClient;
    private final CircuitBreakerRegistry circuitBreakerRegistry;
    private final RetryRegistry retryRegistry;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(TransferServiceImpl.class);

    public TransferServiceImpl(TransferenciaClient transferenciaClient, CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry) {
        this.transferenciaClient = transferenciaClient;
        this.circuitBreakerRegistry = circuitBreakerRegistry;
        this.retryRegistry = retryRegistry;
    }


    @Override
    public TransferResponse transfer(Transfer body) {
        try {
            CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker("transferenciaClient");
            Retry retry = retryRegistry.retry("enviarNotificacao", "default");

            logger.info("Iniciando transferência");
            logger.info("id do cliente {}: ", body.idCliente());
            validarTransferencia(body, circuitBreaker, retry);
            logger.info("Transferência validada");
            UUID id = UUID.randomUUID();

            transferenciaClient.enviarNotificacao(body);


            return new TransferResponse(HttpStatus.CREATED, id.toString());
        } catch (TransferException e) {
            return new TransferResponse(e.getStatus(), e.getMessage());
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private void validarTransferencia(Transfer body, CircuitBreaker circuitBreaker, Retry retry) throws Throwable {
        if (body.conta().idDestino().equals(body.conta().idOrigem())) throw new TransferException("Conta de origem e destino não podem ser iguais", HttpStatus.BAD_REQUEST);
        if (body.valor() <= 0) throw new TransferException("Valor da transferência deve ser maior que zero", HttpStatus.BAD_REQUEST);

        // Verifica se o cliente existe, caso nao exista a exceção é lançada pelo ErrorDecoder que esta na pasta do feign
        logger.info("Buscando cliente e conta de origem");
        CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> transferenciaClient.getClientById(body.idCliente())).get();
        ResponseEntity<ContaResponse> conta = CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> transferenciaClient.getContaByIdOrigem(body.conta().idOrigem())).get();
        validarContaOrigem(conta.getBody(), body.valor());
    }

    private void validarContaOrigem(ContaResponse conta, double valor) {
        logger.info("Validação conta");
        if (conta == null) throw new TransferException("Conta de origem não encontrada", HttpStatus.NOT_FOUND);
        if (!conta.ativo()) throw new TransferException("Conta de origem não se encontra ativa", HttpStatus.BAD_REQUEST);
        if (conta.limiteDiario() < valor) throw new TransferException("Valor da transferência maior que o limite diário", HttpStatus.BAD_REQUEST);
        if (conta.saldo() < valor) throw new TransferException("Saldo insuficiente", HttpStatus.BAD_REQUEST);
    }
}
