package com.blitzar.banktransfer.service;

import com.blitzar.banktransfer.domain.BankTransaction;
import com.blitzar.banktransfer.repository.BankTransferRepository;
import com.blitzar.banktransfer.service.events.BankTransferEvent;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;

import javax.transaction.Transactional;
import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.time.Clock;
import java.time.LocalDateTime;

@Singleton
@Transactional
public class BankTransferService {

    private final BankTransferRepository repository;
    private final Clock currentInstant;
    private final Validator validator;

    @Inject
    public BankTransferService(BankTransferRepository repository, Clock currentInstant, Validator validator) {
        this.repository = repository;
        this.currentInstant = currentInstant;
        this.validator = validator;
    }

    public void registerBankTransfer(BankTransferEvent bankTransferEvent){
        var bankTransferViolations = validator.validate(bankTransferEvent);
        if(!bankTransferViolations.isEmpty()){
            throw new ConstraintViolationException(bankTransferViolations);
        }

        var bankTransfer = new BankTransaction();

        bankTransfer.setAccountFromIBAN(bankTransferEvent.getAccountFromIBAN());
        bankTransfer.setTransactionAmount(bankTransferEvent.getTransferValue());
        bankTransfer.setAccountToIBAN(bankTransferEvent.getAccountToIBAN());
        bankTransfer.setTransactionDate(LocalDateTime.now(currentInstant));
        bankTransfer.setReference(bankTransferEvent.getReference());

        repository.save(bankTransfer);
    }
}