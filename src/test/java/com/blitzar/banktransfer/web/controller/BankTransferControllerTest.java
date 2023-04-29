package com.blitzar.banktransfer.web.controller;

import com.blitzar.banktransfer.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.banktransfer.service.events.BankTransferEvent;
import com.blitzar.banktransfer.web.KafkaTestContainer;
import com.blitzar.banktransfer.web.MySQLTestContainer;
import io.restassured.RestAssured;
import io.restassured.builder.RequestSpecBuilder;
import io.restassured.http.ContentType;
import io.restassured.specification.RequestSpecification;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.SpringBootTest.WebEnvironment;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;

import java.math.BigDecimal;
import java.util.Locale;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasSize;

@SpringBootTest(webEnvironment = WebEnvironment.RANDOM_PORT)
public class BankTransferControllerTest implements MySQLTestContainer, KafkaTestContainer {

    @Autowired
    @Qualifier("exceptionMessageSource")
    private MessageSource exceptionMessageSource;

    private RequestSpecification requestSpecification;

    @BeforeAll
    public static void beforeAll(@LocalServerPort int serverHttpPort){
        RestAssured.port = serverHttpPort;
        RestAssured.basePath = "/api/v1/bank-transfers";
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
    }

    @BeforeEach
    public void beforeEach() {
        this.requestSpecification = new RequestSpecBuilder()
                .setContentType(ContentType.JSON)
                .build();
    }

    @Test
    public void givenValidRequest_whenRegisterBankTransfer_thenSuccess(){
        var bankTransferEvent = new BankTransferEvent("ES6531908221216475895468", BigDecimal.TEN, "DE79500105177228677684", "Payment");

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.ACCEPTED.value());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountFromIBAN_whenRegisterBankTransfer_thenThrowException(String invalidAccountFromIBAN){
        var bankTransferEvent = new BankTransferEvent(invalidAccountFromIBAN, BigDecimal.TEN, "DE79500105177228677684", "Payment");

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("instance", equalTo(RestAssured.basePath))
                .body("errors", hasSize(1))
                    .body("errors[0].field", equalTo("accountFromIBAN"))
                    .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("bankTransfer.accountFromIBAN.notBlank", null, Locale.getDefault())));
    }

    @Test
    public void givenNullTransferValue_whenRegisterBankTransfer_thenThrowException(){
        var bankTransferEvent = new BankTransferEvent("ES6531908221216475895468", null, "DE79500105177228677684", "Payment");

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("instance", equalTo(RestAssured.basePath))
                .body("errors", hasSize(1))
                    .body("errors[0].field", equalTo("transferValue"))
                    .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("bankTransfer.transferValue.notNull", null, Locale.getDefault())));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "0.00"})
    public void givenNotPositiveTransferValue_whenRegisterBankTransfer_thenThrowException(String stringTransferValue){
        BigDecimal transferValue = new BigDecimal(stringTransferValue);
        var bankTransferEvent = new BankTransferEvent("ES6531908221216475895468", transferValue, "DE79500105177228677684", "Payment");

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("instance", equalTo(RestAssured.basePath))
                .body("errors", hasSize(1))
                    .body("errors[0].field", equalTo("transferValue"))
                    .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("bankTransfer.transferValue.positive", null, Locale.getDefault())));
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountToIBAN_whenRegisterBankTransfer_thenThrowException(String invalidAccountToIBAN){
        var bankTransferEvent = new BankTransferEvent("ES6531908221216475895468", BigDecimal.TEN, invalidAccountToIBAN, "Payment");

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("instance", equalTo(RestAssured.basePath))
                .body("errors", hasSize(1))
                    .body("errors[0].field", equalTo("accountToIBAN"))
                    .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("bankTransfer.accountToIBAN.notBlank", null, Locale.getDefault())));
    }

    @Test
    public void givenReferenceLongerThan30Characters_whenRegisterBankTransfer_thenThrowException(){
        var invalidBankTransferReference = RandomStringUtils.randomAlphabetic(31);
        var bankTransferEvent = new BankTransferEvent("ES6531908221216475895468", BigDecimal.TEN, "DE79500105177228677684", invalidBankTransferReference);

        given()
            .spec(requestSpecification)
            .body(bankTransferEvent)
        .when()
            .post()
        .then()
            .statusCode(HttpStatus.BAD_REQUEST.value())
                .body("title", equalTo(HttpStatus.BAD_REQUEST.getReasonPhrase()))
                .body("status", equalTo(HttpStatus.BAD_REQUEST.value()))
                .body("instance", equalTo(RestAssured.basePath))
                .body("errors", hasSize(1))
                    .body("errors[0].field", equalTo("reference"))
                    .body("errors[0].message", equalTo(exceptionMessageSource.getMessage("bankTransfer.reference.length.limit", null, Locale.getDefault())));
    }
}
