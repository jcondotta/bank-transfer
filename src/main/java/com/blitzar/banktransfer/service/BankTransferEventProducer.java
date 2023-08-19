package com.blitzar.banktransfer.service;

import com.blitzar.banktransfer.service.events.BankTransferEvent;
import io.micronaut.jms.annotations.JMSProducer;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.jms.sqs.configuration.SqsConfiguration;
import io.micronaut.messaging.annotation.MessageBody;

@JMSProducer(SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME)
public interface BankTransferEventProducer {

    @Queue("${app.aws.sqs.bank-transfer-queue-name}")
    void sendMessage(@MessageBody BankTransferEvent bankTransferEvent);
}