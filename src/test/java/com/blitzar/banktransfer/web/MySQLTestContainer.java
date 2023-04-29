package com.blitzar.banktransfer.web;

import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;

@Testcontainers
public interface MySQLTestContainer {

    String mySQLImageName = "mysql:8.0";
    String databaseName = "integration-test-db";

    MySQLContainer<?> MYSQL_CONTAINER = (MySQLContainer) new MySQLContainer(mySQLImageName)
                .withDatabaseName(databaseName);

    @DynamicPropertySource
    static void registerMySQLProperties(DynamicPropertyRegistry registry) {
        Startables.deepStart(MYSQL_CONTAINER).join();

        registry.add("spring.datasource.url", MYSQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", MYSQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", MYSQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", MYSQL_CONTAINER::getDriverClassName);
    }
}


