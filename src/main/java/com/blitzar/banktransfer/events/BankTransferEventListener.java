package com.blitzar.banktransfer.events;

import com.blitzar.banktransfer.service.BankTransferEventProducer;
import com.blitzar.banktransfer.service.BankTransferService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class BankTransferEventListener {

    private static final Logger logger = LoggerFactory.getLogger(BankTransferEventProducer.class);

    private final BankTransferService bankTransferService;

    @Autowired
    public BankTransferEventListener(BankTransferService bankTransferService) {
        this.bankTransferService = bankTransferService;
    }

    @KafkaListener(topics = "${app.kafka.bank-transfer-topic}", groupId = "${app.kafka.bank-transfer-topic-group-id}")
    public void listener(BankTransferEvent bankTransferEvent){
        logger.info("Producing a event to transfer: {} from account: {} to: {}", bankTransferEvent.transferValue(), bankTransferEvent.accountFromIBAN(), bankTransferEvent.accountToIBAN());

        bankTransferService.registerBankTransfer(bankTransferEvent);
    }
}
