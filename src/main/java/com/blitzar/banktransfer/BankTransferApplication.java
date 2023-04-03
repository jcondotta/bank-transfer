package com.blitzar.banktransfer;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;

@SpringBootApplication
@ConfigurationPropertiesScan
public class BankTransferApplication {

    public static void main(String[] args) {
        SpringApplication.run(BankTransferApplication.class, args);
    }

}
