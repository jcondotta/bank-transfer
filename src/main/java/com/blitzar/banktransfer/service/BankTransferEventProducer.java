package com.blitzar.banktransfer.service;

import com.blitzar.banktransfer.config.kafka.KafkaApplicationProperties;
import com.blitzar.banktransfer.service.events.BankTransferEvent;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class BankTransferEventProducer {

    private static final Logger logger = LoggerFactory.getLogger(BankTransferEventProducer.class);

    private final KafkaTemplate<String, BankTransferEvent> kafkaTemplate;
    private final KafkaApplicationProperties kafkaApplicationProperties;

    public BankTransferEventProducer(KafkaTemplate<String, BankTransferEvent> kafkaTemplate, KafkaApplicationProperties kafkaApplicationProperties) {
        this.kafkaApplicationProperties = kafkaApplicationProperties;
        this.kafkaTemplate = kafkaTemplate;
    }

    public void handle(BankTransferEvent bankTransferEvent){
        logger.info("Producing a bank transfer of: €{} from account: {} to account: {}", bankTransferEvent.transferValue(), bankTransferEvent.accountFromIBAN(), bankTransferEvent.accountToIBAN());

        kafkaTemplate.send(kafkaApplicationProperties.bankTransferTopic(), bankTransferEvent);
    }
}