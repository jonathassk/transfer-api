package com.example.transfer.service;

import com.example.transfer.exceptions.TransferException;
import com.example.transfer.feign.TransferenciaClient;
import com.example.transfer.model.ContaResponse;
import com.example.transfer.model.Transfer;
import com.example.transfer.model.TransferResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class TransferServiceImpl implements TransferService{

    private final TransferenciaClient transferenciaClient;
    private final CircuitBreaker circuitBreaker;

    private final Logger logger = org.slf4j.LoggerFactory.getLogger(TransferServiceImpl.class);

    public TransferServiceImpl(TransferenciaClient transferenciaClient, CircuitBreakerRegistry circuitBreakerRegistry) {
        this.transferenciaClient = transferenciaClient;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("transferService");

    }

    @Override
    public TransferResponse transfer(Transfer body) {
        try {
            logger.info("Iniciando transferência");
            logger.info("id do cliente {}: ", body.idCliente());
            validarTransferencia(body);
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

    private void validarTransferencia(Transfer body) throws Throwable {
        if (body.conta().idDestino().equals(body.conta().idOrigem())) throw new TransferException("Conta de origem e destino não podem ser iguais", HttpStatus.BAD_REQUEST);
        if (body.valor() <= 0) throw new TransferException("Valor da transferência deve ser maior que zero", HttpStatus.BAD_REQUEST);

        // Verifica se o cliente existe, caso nao exista a exceção é lançada pelo ErrorDecoder que esta na pasta do feign
        logger.info("Buscando cliente e conta de origem");
        transferenciaClient.getClientById(body.idCliente());
        ResponseEntity<ContaResponse> conta = CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> transferenciaClient.getContaByIdOrigem(body.conta().idOrigem())).apply();

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
