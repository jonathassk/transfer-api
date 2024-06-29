package com.example.transfer.feign;

import com.example.transfer.config.FeignConfig;
import com.example.transfer.model.BacenResponse;
import com.example.transfer.model.ContaResponse;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;

@FeignClient(name = "TransferenciaServiceClient", url = "http://localhost:9090",  configuration = FeignConfig.class)
public interface TransferenciaClient {

    @GetMapping("/clientes/{id}")
    void getClientById(@PathVariable("id") String id);

    @GetMapping("/contas/{id}")
    ResponseEntity<ContaResponse> getContaByIdOrigem(@PathVariable("id") String id);

    @PostMapping("/notificacoes")
    ResponseEntity<BacenResponse> enviarNotificacao();



}
