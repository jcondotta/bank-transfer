package com.blitzar.banktransfer.service;

import com.blitzar.banktransfer.argumentprovider.InvalidStringArgumentProvider;
import com.blitzar.banktransfer.repository.BankTransferRepository;
import com.blitzar.banktransfer.service.events.BankTransferEvent;
import org.apache.commons.lang3.RandomStringUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ArgumentsSource;
import org.junit.jupiter.params.provider.ValueSource;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.validation.ConstraintViolationException;
import javax.validation.Validation;
import java.math.BigDecimal;
import java.time.Clock;
import java.time.ZoneOffset;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertAll;
import static org.junit.jupiter.api.Assertions.assertThrowsExactly;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

@ExtendWith(MockitoExtension.class)
class BankTransferServiceTest {

    private BankTransferService bankTransferService;

    @Mock
    private BankTransferRepository bankTransferRepositoryMock;

    private String accountFromIBAN = "ES6531908221216475895468";
    private BigDecimal transferValue = BigDecimal.TEN;
    private String accountToIBAN = "DE79500105177228677684";
    private String reference = "Donate";

    @BeforeEach
    public void beforeEach(){
        var validator = Validation.buildDefaultValidatorFactory().getValidator();
        bankTransferService = new BankTransferService(bankTransferRepositoryMock, Clock.system(ZoneOffset.UTC), validator);
    }

    @Test
    public void givenValidRequest_whenRegisterBankTransfer_thenSuccess(){
        var bankTransferEvent = new BankTransferEvent(accountFromIBAN, transferValue, accountToIBAN, reference);

        bankTransferService.registerBankTransfer(bankTransferEvent);
        verify(bankTransferRepositoryMock).save(any());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountFromIBAN_whenRegisterBankTransfer_thenThrowException(String invalidAccountFromIBAN){
        var bankTransferEvent = new BankTransferEvent(invalidAccountFromIBAN, transferValue, accountToIBAN, reference);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> bankTransferService.registerBankTransfer(bankTransferEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("bankTransfer.accountFromIBAN.notBlank"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("accountFromIBAN")
                ));

        verify(bankTransferRepositoryMock, never()).save(any());
    }

    @Test
    public void givenNullTransferValue_whenRegisterBankTransfer_thenThrowException(){
        var bankTransferEvent = new BankTransferEvent(accountFromIBAN, null, accountToIBAN, reference);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> bankTransferService.registerBankTransfer(bankTransferEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("bankTransfer.transferValue.notNull"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("transferValue")
                ));

        verify(bankTransferRepositoryMock, never()).save(any());
    }

    @ParameterizedTest
    @ValueSource(strings = {"-0.01", "0.00"})
    public void givenNotPositiveTransferValue_whenRegisterBankTransfer_thenThrowException(String stringTransferValue){
        BigDecimal invalidTransferValue = new BigDecimal(stringTransferValue);
        var bankTransferEvent = new BankTransferEvent(accountFromIBAN, invalidTransferValue, accountToIBAN, reference);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> bankTransferService.registerBankTransfer(bankTransferEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("bankTransfer.transferValue.positive"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("transferValue")
                ));

        verify(bankTransferRepositoryMock, never()).save(any());
    }

    @ParameterizedTest
    @ArgumentsSource(InvalidStringArgumentProvider.class)
    public void givenInvalidAccountToIBAN_whenRegisterBankTransfer_thenThrowException(String invalidAccountToIBAN){
        var bankTransferEvent = new BankTransferEvent(accountFromIBAN, transferValue, invalidAccountToIBAN, reference);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> bankTransferService.registerBankTransfer(bankTransferEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("bankTransfer.accountToIBAN.notBlank"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("accountToIBAN")
                ));

        verify(bankTransferRepositoryMock, never()).save(any());
    }

    @Test
    public void givenReferenceLongerThan30Characters_whenRegisterBankTransfer_thenThrowException(){
        var invalidBankTransferReference = RandomStringUtils.randomAlphabetic(31);
        var bankTransferEvent = new BankTransferEvent(accountFromIBAN, transferValue, accountToIBAN, invalidBankTransferReference);

        var exception = assertThrowsExactly(ConstraintViolationException.class, () -> bankTransferService.registerBankTransfer(bankTransferEvent));
        assertThat(exception.getConstraintViolations()).hasSize(1);

        exception.getConstraintViolations().stream()
                .findFirst()
                .ifPresent(violation -> assertAll(
                        () -> assertThat(violation.getMessage()).isEqualTo("bankTransfer.reference.length.limit"),
                        () -> assertThat(violation.getPropertyPath().toString()).isEqualTo("reference")
                ));

        verify(bankTransferRepositoryMock, never()).save(any());
    }
}
