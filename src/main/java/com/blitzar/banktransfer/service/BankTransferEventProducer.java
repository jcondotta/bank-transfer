package com.blitzar.banktransfer.service;

import com.blitzar.banktransfer.config.kafka.KafkaApplicationProperties;
import com.blitzar.banktransfer.events.BankTransferEvent;
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
        logger.info("Producing a event to transfer:{} from account:{} to:{}", bankTransferEvent.transferValue(), bankTransferEvent.accountFromIBAN(), bankTransferEvent.accountToIBAN());
        kafkaTemplate.send(kafkaApplicationProperties.bankTransferTopic(), bankTransferEvent);
    }
}