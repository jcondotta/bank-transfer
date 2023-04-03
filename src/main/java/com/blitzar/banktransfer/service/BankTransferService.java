package com.blitzar.banktransfer.service;

import com.blitzar.banktransfer.domain.BankTransfer;
import com.blitzar.banktransfer.events.BankTransferEvent;
import com.blitzar.banktransfer.repository.BankTransferRepository;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.Clock;
import java.time.LocalDateTime;

@Service
public class BankTransferService {

    private final BankTransferRepository repository;
    private final Clock currentInstant;

    @Autowired
    public BankTransferService(BankTransferRepository repository, Clock currentInstant) {
        this.repository = repository;
        this.currentInstant = currentInstant;
    }

    public void registerBankTransfer(@Valid BankTransferEvent bankTransferEvent){
        var bankTransfer = new BankTransfer();

        bankTransfer.setAccountFromIBAN(bankTransferEvent.accountFromIBAN());
        bankTransfer.setTransferValue(bankTransferEvent.transferValue());
        bankTransfer.setAccountToIBAN(bankTransferEvent.accountToIBAN());
        bankTransfer.setTransferDate(LocalDateTime.now(currentInstant));
        bankTransfer.setReference(bankTransferEvent.reference());

        repository.save(bankTransfer);
    }
}