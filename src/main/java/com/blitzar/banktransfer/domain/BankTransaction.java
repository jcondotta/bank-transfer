package com.blitzar.banktransfer.domain;

import javax.persistence.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "bank_transaction")
public class BankTransaction {

    @Id
    @Column(name = "bank_transaction_id")
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long bankTransactionId;

    @Column(name = "account_from_iban", nullable = false)
    private String accountFromIBAN;

    @Column(name = "transaction_amount", nullable = false)
    private BigDecimal transactionAmount;

    @Column(name = "account_to_iban", nullable = false)
    private String accountToIBAN;

    @Enumerated(EnumType.STRING)
    @Column(name = "transaction_type", nullable = false)
    private TransactionType transactionType;

    @Column(name = "transaction_date", nullable = false)
    private LocalDateTime transactionDate;

    @Column(name = "reference")
    private String reference;

    public Long getBankTransactionId() {
        return bankTransactionId;
    }

    public void setBankTransactionId(Long bankTransferId) {
        this.bankTransactionId = bankTransferId;
    }

    public String getAccountFromIBAN() {
        return accountFromIBAN;
    }

    public void setAccountFromIBAN(String accountFromIBAN) {
        this.accountFromIBAN = accountFromIBAN;
    }

    public BigDecimal getTransactionAmount() {
        return transactionAmount;
    }

    public void setTransactionAmount(BigDecimal transferValue) {
        this.transactionAmount = transferValue;
    }

    public String getAccountToIBAN() {
        return accountToIBAN;
    }

    public void setAccountToIBAN(String accountToIBAN) {
        this.accountToIBAN = accountToIBAN;
    }

    public TransactionType getTransactionType() {
        return transactionType;
    }

    public void setTransactionType(TransactionType transactionType) {
        this.transactionType = transactionType;
    }

    public LocalDateTime getTransactionDate() {
        return transactionDate;
    }

    public void setTransactionDate(LocalDateTime transferDate) {
        this.transactionDate = transferDate;
    }

    public String getReference() {
        return reference;
    }

    public void setReference(String reference) {
        this.reference = reference;
    }
}
