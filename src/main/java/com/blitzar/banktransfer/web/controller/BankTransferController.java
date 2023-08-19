package com.blitzar.banktransfer.web.controller;

import com.blitzar.banktransfer.service.BankTransferEventProducer;
import com.blitzar.banktransfer.service.events.BankTransferEvent;
import io.micronaut.context.MessageSource;
import io.micronaut.http.HttpResponse;
import io.micronaut.http.HttpStatus;
import io.micronaut.http.MediaType;
import io.micronaut.http.annotation.Body;
import io.micronaut.http.annotation.Controller;
import io.micronaut.http.annotation.Post;
import io.micronaut.http.annotation.Status;
import io.micronaut.validation.Validated;
import jakarta.inject.Inject;

import javax.validation.ConstraintViolationException;
import javax.validation.Validator;
import java.util.Locale;

@Validated
@Controller(BankTransferAPIConstants.BASE_PATH_API_V1_MAPPING)
public class BankTransferController {

    private final BankTransferEventProducer bankTransferEventProducer;
    private final Validator validator;
    private final MessageSource messageSource;

    @Inject
    public BankTransferController(BankTransferEventProducer bankTransferEventProducer, Validator validator, MessageSource messageSource) {
        this.bankTransferEventProducer = bankTransferEventProducer;
        this.validator = validator;
        this.messageSource = messageSource;
    }

    @Status(HttpStatus.ACCEPTED)
    @Post(consumes = MediaType.APPLICATION_JSON)
    public HttpResponse<?> registerBankTransfer(@Body BankTransferEvent bankTransferEvent){
        var constraintViolations = validator.validate(bankTransferEvent);
        if(!constraintViolations.isEmpty()){
            throw new ConstraintViolationException(constraintViolations);
        }

        bankTransferEventProducer.sendMessage(bankTransferEvent);

        var bankTransferAcceptedMessage = messageSource.getMessage("bank-transfer.accepted", Locale.getDefault()).orElse(null);
        return HttpResponse.accepted().body(bankTransferAcceptedMessage);
    }
}
