package com.blitzar.banktransfer.service.events;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;

public record BankTransferEvent(
        @NotBlank(message = "bankTransfer.accountFromIBAN.notBlank")
        String accountFromIBAN,

        @NotNull(message = "bankTransfer.transferValue.notNull")
        @Positive(message = "bankTransfer.transferValue.positive")
        BigDecimal transferValue,

        @NotBlank(message = "bankTransfer.accountToIBAN.notBlank")
        String accountToIBAN,

        @Size(max = 30, message = "bankTransfer.reference.length.limit")
        String reference
) { }

