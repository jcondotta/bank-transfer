package com.blitzar.banktransfer.web.controller;

import com.blitzar.banktransfer.service.events.BankTransferEvent;
import com.blitzar.banktransfer.service.BankTransferEventProducer;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.WebRequest;

@RestController
@RequestMapping("/api/v1/bank-transfers")
public class BankTransferController {

    private BankTransferEventProducer bankTransferEventProducer;

    @Autowired
    public BankTransferController(BankTransferEventProducer bankTransferEventProducer) {
        this.bankTransferEventProducer = bankTransferEventProducer;
    }

    @ResponseStatus(HttpStatus.ACCEPTED)
    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> registerBankTransfer(@Valid @RequestBody BankTransferEvent bankTransferEvent, WebRequest request){
        bankTransferEventProducer.handle(bankTransferEvent);

        return ResponseEntity.accepted().build();
    }
}
