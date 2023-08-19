package com.blitzar.banktransfer.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
public class BankTransfer {

    @Id
    @Column(name = "bank_transfer_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankTransferId;

    @Column(name = "account_from_iban", nullable = false)
    private String accountFromIBAN;

    @Column(name = "transfer_value", nullable = false)
    private BigDecimal transferValue;

    @Column(name = "account_to_iban", nullable = false)
    private String accountToIBAN;

    @Column(name = "transfer_date")
    private LocalDateTime transferDate;

    @Column(name = "reference")
    private String reference;

    public Long getBankTransferId() {
        return bankTransferId;
    }

    public void setBankTransferId(Long bankTransferId) {
        this.bankTransferId = bankTransferId;
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

    public LocalDateTime getTransferDate() {
        return transferDate;
    }

    public void setTransferDate(LocalDateTime transferDate) {
        this.transferDate = transferDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
