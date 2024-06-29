package com.example.transfer.service;

import com.example.transfer.exceptions.TransferException;
import com.example.transfer.feign.TransferenciaClient;
import com.example.transfer.model.BacenResponse;
import com.example.transfer.model.ContaResponse;
import com.example.transfer.model.Transfer;
import com.example.transfer.model.TransferResponse;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;
import org.slf4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.Duration;

@Service
public class TransferServiceImpl implements TransferService{

    private final TransferenciaClient transferenciaClient;
    private final CircuitBreaker circuitBreaker;
    private final Retry retry;
    private final Logger logger = org.slf4j.LoggerFactory.getLogger(TransferServiceImpl.class);

    public TransferServiceImpl(TransferenciaClient transferenciaClient, CircuitBreakerRegistry circuitBreakerRegistry, RetryRegistry retryRegistry) {
        this.transferenciaClient = transferenciaClient;
        this.circuitBreaker = circuitBreakerRegistry.circuitBreaker("transferService");
        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(5000))
                .retryExceptions(TransferException.class)
                .build();
        this.retry = Retry.of("transferService", () -> retryConfig);
    }

    @Override
    public TransferResponse transfer(Transfer body) {
        try {
            logger.info("Iniciando transferência");
            validarTransferencia(body);
            logger.info("Transferência validada");

            // requisição com circuit breaker e retry de 3x de 5 segundos entre cada uma
            CircuitBreaker.decorateCheckedSupplier(circuitBreaker, () -> {
                ResponseEntity<BacenResponse> response = enviarNotificacaoBacen();
                if (response.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
                    //transferRepository.save(new TransferDTO(body.idCliente(), Status.BACEN_ERROR, body.valor()));
                    throw new TransferException("Transferencia realizada, Não notificada ao bacen", HttpStatus.TOO_MANY_REQUESTS);
                }
                return response;
            }).apply();

            return new TransferResponse(HttpStatus.CREATED, "Transferência realizada com sucesso");
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

    private ResponseEntity<BacenResponse> enviarNotificacaoBacen() {
        logger.info("enviando ao bacen");
        ResponseEntity<BacenResponse> notificacao = transferenciaClient.enviarNotificacao();
        if (notificacao.getStatusCode() == HttpStatus.TOO_MANY_REQUESTS) {
            logger.warn("Bacen retornou 429, tentando enviar novamente!");
            try {
                Thread.sleep(5000);
                notificacao = transferenciaClient.enviarNotificacao();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                throw new TransferException("Erro ao aguardar para reenviar notificação", HttpStatus.INTERNAL_SERVER_ERROR);
            }
        }
        return notificacao;
    }
}
