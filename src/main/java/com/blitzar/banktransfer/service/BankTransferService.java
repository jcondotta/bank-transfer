package com.blitzar.banktransfer.service;

import com.blitzar.banktransfer.domain.BankTransfer;
import com.blitzar.banktransfer.service.events.BankTransferEvent;
import com.blitzar.banktransfer.repository.BankTransferRepository;
import jakarta.validation.ConstraintViolationException;
import jakarta.validation.Valid;
import jakarta.validation.Validator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class BankTransferService {

    private final BankTransferRepository repository;
    private final Clock currentInstant;
    private final Validator validator;

    @Autowired
    public BankTransferService(BankTransferRepository repository, Clock currentInstant, Validator validator) {
        this.repository = repository;
        this.currentInstant = currentInstant;
        this.validator = validator;
    }

    public void registerBankTransfer(@Valid BankTransferEvent bankTransferEvent){
        var bankTransferViolations = validator.validate(bankTransferEvent);
        if(!bankTransferViolations.isEmpty()){
            throw new ConstraintViolationException(bankTransferViolations);
        }

        var bankTransfer = new BankTransfer();

        bankTransfer.setAccountFromIBAN(bankTransferEvent.accountFromIBAN());
        bankTransfer.setTransferValue(bankTransferEvent.transferValue());
        bankTransfer.setAccountToIBAN(bankTransferEvent.accountToIBAN());
        bankTransfer.setTransferDate(LocalDateTime.now(currentInstant));
        bankTransfer.setReference(bankTransferEvent.reference());

        repository.save(bankTransfer);
    }
}