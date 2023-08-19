package com.blitzar.banktransfer.service.events;

import com.blitzar.banktransfer.service.BankTransferService;
import io.micronaut.jms.annotations.JMSListener;
import io.micronaut.jms.annotations.Queue;
import io.micronaut.jms.sqs.configuration.SqsConfiguration;
import io.micronaut.messaging.annotation.MessageBody;
import jakarta.inject.Inject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@JMSListener(SqsConfiguration.CONNECTION_FACTORY_BEAN_NAME)
public class BankTransferEventConsumer {

    private static final Logger logger = LoggerFactory.getLogger(BankTransferEventConsumer.class);

    private final BankTransferService bankTransferService;

    @Inject
    public BankTransferEventConsumer(BankTransferService bankTransferService) {
        this.bankTransferService = bankTransferService;
    }

    @Queue(value = "${app.aws.sqs.bank-transfer-queue-name}", concurrency = "1-3")
    public void consumeMessage(@MessageBody BankTransferEvent bankTransferEvent) {
        logger.info("Registering a bank transfer of: €{} from account: {} to account: {}", bankTransferEvent.getTransferValue(),
                bankTransferEvent.getAccountFromIBAN(), bankTransferEvent.getAccountToIBAN());

        bankTransferService.registerBankTransfer(bankTransferEvent);
    }
}