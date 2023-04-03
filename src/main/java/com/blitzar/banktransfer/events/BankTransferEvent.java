package com.blitzar.banktransfer.events;

import java.math.BigDecimal;

public record BankTransferEvent(String accountFromIBAN, BigDecimal transferValue, String accountToIBAN, String reference) {
}

