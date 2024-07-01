package com.example.transfer.controller;

import com.example.transfer.exceptions.TransferException;
import com.example.transfer.service.TransferService;
import com.example.transfer.model.Transfer;
import com.example.transfer.model.TransferResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.ExampleObject;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Optional;

@RestController
@RequestMapping(value = "/", produces = "application/json")
@Tag(name = "Transfer", description = "API de transferências")
public class TransferController {

    private final TransferService transferService;

    public TransferController(TransferService transferService) {
        this.transferService = transferService;
    }


    @Operation(summary = "Realiza a transferência entre contas", method = "POST")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "201", description = "Transferência realizada com sucesso", content = @Content(examples = {
                    @ExampleObject(value = "{\"id_transferencia\": \"ID\"}")
            })),
            @ApiResponse(responseCode = "400", description = "Erro na requisição", content = @Content(examples = {
                    @ExampleObject(value = "{\"field\": \"message\"}")
            })),
            @ApiResponse(responseCode = "404", description = "Conta não encontrada", content = @Content(examples = {
                    @ExampleObject(value = "Conta não encontrada")
            })),
            @ApiResponse(responseCode = "500", description = "Erro interno no servidor", content = @Content(examples = {
                    @ExampleObject(value = "Erro interno no servidor")
            })),
    })
    @PostMapping("transferencia")
    public ResponseEntity<?> transfer(@Valid @RequestBody Transfer body, BindingResult bindingResult) {
        Optional<HashMap<String, String>> errors = validateFields(bindingResult);
        if (errors.isPresent()) return ResponseEntity.badRequest().body(errors);
        try {
            TransferResponse idCliente = transferService.transfer(body);
            return ResponseEntity.status(HttpStatus.CREATED).body(idCliente.message());
        } catch (TransferException e) {
            return ResponseEntity.status(e.getStatus()).body(e.getMessage());
        }


    }
    private Optional<HashMap<String, String>> validateFields(BindingResult bindingResult) {
        if (bindingResult.hasErrors()) {
            HashMap<String, String> errors = new HashMap<>();
            for (FieldError error : bindingResult.getFieldErrors()) {
                errors.put(error.getField(), error.getDefaultMessage());
            }
            return Optional.of(errors);
        }
        return Optional.empty();
    }
}
