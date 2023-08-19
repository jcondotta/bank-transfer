package com.blitzar.banktransfer.service.events;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Positive;
import javax.validation.constraints.Size;
import java.math.BigDecimal;

public class BankTransferEvent {
    @NotBlank(message = "bankTransfer.accountFromIBAN.notBlank")
    private String accountFromIBAN;

    @NotNull(message = "bankTransfer.transferValue.notNull")
    @Positive(message = "bankTransfer.transferValue.positive")
    private BigDecimal transferValue;

    @NotBlank(message = "bankTransfer.accountToIBAN.notBlank")
    private String accountToIBAN;

    @Size(max = 30, message = "bankTransfer.reference.length.limit")
    private String reference;

    public BankTransferEvent() {}

    public BankTransferEvent(String accountFromIBAN, BigDecimal transferValue, String accountToIBAN, String reference) {
        this.accountFromIBAN = accountFromIBAN;
        this.transferValue = transferValue;
        this.accountToIBAN = accountToIBAN;
        this.reference = reference;
    }

    public BankTransferEvent(String accountFromIBAN, BigDecimal transferValue, String accountToIBAN) {
        this(accountFromIBAN, transferValue, accountToIBAN, null);
    }

    public String getAccountFromIBAN() {
        return accountFromIBAN;
    }

    public void setAccountFromIBAN(String accountFromIBAN) {
        this.accountFromIBAN = accountFromIBAN;
    }

    public BigDecimal getTransferValue() {
        return transferValue;
    }

    public void setTransferValue(BigDecimal transferValue) {
        this.transferValue = transferValue;
    }

    public String getAccountToIBAN() {
        return accountToIBAN;
    }

    public void setAccountToIBAN(String accountToIBAN) {
        this.accountToIBAN = accountToIBAN;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}

