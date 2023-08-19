package com.blitzar.banktransfer.web.controller;

import com.amazonaws.services.sqs.AmazonSQS;
import com.amazonaws.services.sqs.model.CreateQueueRequest;
import com.blitzar.banktransfer.LocalStackMySQLTestContainer;
import com.blitzar.banktransfer.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.banktransfer.service.events.BankTransferEvent;
import io.micronaut.context.MessageSource;
import io.micronaut.context.annotation.Value;
import io.micronaut.http.HttpStatus;
import io.micronaut.test.extensions.junit5.annotation.MicronautTest;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import jakarta.inject.Inject;
import jakarta.inject.Named;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestInstance.Lifecycle;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;

import java.math.BigDecimal;
import java.util.Locale;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@TestInstance(Lifecycle.PER_CLASS)
@MicronautTest(transactional = false)
public class BankTransferControllerTest implements LocalStackMySQLTestContainer {

    @Inject
    @Named("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

    @Inject
    private AmazonSQS sqsClient;

    @Value("${app.aws.sqs.bank-transfer-queue-name}")
    private String bankAccountApplicationQueueName;

    private String bankAccountApplicationQueueURL;

    private RequestSpecification requestSpecification;

    private String accountFromIBAN = "ES6531908221216475895468";
    private BigDecimal transferValue = BigDecimal.TEN;
    private String accountToIBAN = "DE79500105177228677684";
    private String reference = "Donate";

    @BeforeAll
    public static void beforeAll(){
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach(RequestSpecification requestSpecification) {
        this.requestSpecification = requestSpecification
                .contentType(ContentType.JSON)
                .basePath(BankTransferAPIConstants.BASE_PATH_API_V1_MAPPING);

        this.bankAccountApplicationQueueURL = sqsClient.createQueue(bankAccountApplicationQueueName).getQueueUrl();
    }

    @Test
    public void givenValidRequest_whenRegisterBankTransfer_thenSuccess(){
        var bankTransferEvent = new BankTransferEvent(accountFromIBAN, transferValue, accountToIBAN, reference);

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.ACCEPTED.getCode());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountFromIBAN_whenRegisterBankTransfer_thenThrowException(String invalidAccountFromIBAN){
        var bankTransferEvent = new BankTransferEvent(invalidAccountFromIBAN, transferValue, accountToIBAN, reference);

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(exceptionMessageSource.
                        getMessage("bankTransfer.accountFromIBAN.notBlank", Locale.getDefault()).orElseThrow()));
    }

    @Test
    public void givenNullTransferValue_whenRegisterBankTransfer_thenThrowException(){
        var bankTransferEvent = new BankTransferEvent(accountFromIBAN, null, accountToIBAN, reference);

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(exceptionMessageSource.
                        getMessage("bankTransfer.transferValue.notNull", Locale.getDefault()).orElseThrow()));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "0.00"})
    public void givenNotPositiveTransferValue_whenRegisterBankTransfer_thenThrowException(String stringTransferValue){
        BigDecimal invalidTransferValue = new BigDecimal(stringTransferValue);
        var bankTransferEvent = new BankTransferEvent(accountFromIBAN, invalidTransferValue, accountToIBAN, reference);

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(exceptionMessageSource.
                        getMessage("bankTransfer.transferValue.positive", Locale.getDefault()).orElseThrow()));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountToIBAN_whenRegisterBankTransfer_thenThrowException(String invalidAccountToIBAN){
        var bankTransferEvent = new BankTransferEvent(accountFromIBAN, transferValue, invalidAccountToIBAN, reference);

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(exceptionMessageSource.
                        getMessage("bankTransfer.accountToIBAN.notBlank", Locale.getDefault()).orElseThrow()));
    }

    @Test
    public void givenReferenceLongerThan30Characters_whenRegisterBankTransfer_thenThrowException(){
        var invalidBankTransferReference = RandomStringUtils.randomAlphabetic(31);
        var bankTransferEvent = new BankTransferEvent(accountFromIBAN, transferValue, accountToIBAN, invalidBankTransferReference);

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.getCode())
            .rootPath("_embedded")
                .body("errors", hasSize(1))
                .body("errors[0].message", equalTo(exceptionMessageSource.
                        getMessage("bankTransfer.reference.length.limit", Locale.getDefault()).orElseThrow()));
    }
}
