package com.blitzar.banktransfer;

import io.micronaut.test.support.TestPropertyProvider;
import org.testcontainers.containers.MySQLContainer;
import org.testcontainers.containers.localstack.LocalStackContainer;
import org.testcontainers.containers.localstack.LocalStackContainer.Service;
import org.testcontainers.junit.jupiter.Testcontainers;
import org.testcontainers.lifecycle.Startables;
import org.testcontainers.utility.DockerImageName;

import java.io.IOException;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Testcontainers
public interface LocalStackMySQLTestContainer extends TestPropertyProvider {

    String localStackImageName = "localstack/localstack:2.1.0";
    DockerImageName LOCALSTACK_IMAGE = DockerImageName.parse(localStackImageName);

    String mySQLImageName = "mysql:8.0";
    String databaseName = "integration-test-db";

    LocalStackContainer LOCALSTACK_CONTAINER = new LocalStackContainer(LOCALSTACK_IMAGE)
            .withServices(Service.SQS)
            .withServices(Service.SNS)
            .withReuse(true);

    MySQLContainer<?> MYSQL_CONTAINER = (MySQLContainer) new MySQLContainer(mySQLImageName)
            .withDatabaseName(databaseName)
            .withReuse(true);

    @Override
    default Map<String, String> getProperties() {
        Startables.deepStart(LOCALSTACK_CONTAINER, MYSQL_CONTAINER).join();

        try {
            LOCALSTACK_CONTAINER.execInContainer("awslocal", "sqs", "create-queue", "--queue-name", "bank-transfer");
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        return Stream.of(getMySQLProperties(), getAWSProperties())
                .flatMap(property -> property.entrySet().stream())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue));
    }

    default Map<String, String> getAWSProperties() {
        return Map.of(
                "AWS_ACCESS_KEY_ID", LOCALSTACK_CONTAINER.getAccessKey(),
                "AWS_SECRET_ACCESS_KEY", LOCALSTACK_CONTAINER.getSecretKey(),
                "AWS_DEFAULT_REGION", LOCALSTACK_CONTAINER.getRegion(),
                "AWS_SQS_ENDPOINT", LOCALSTACK_CONTAINER.getEndpointOverride(Service.SQS).toString());
    }

    default Map<String, String> getMySQLProperties() {
        return Map.of(
                "database.url", MYSQL_CONTAINER.getJdbcUrl(),
                "database.username", MYSQL_CONTAINER.getUsername(),
                "database.password", MYSQL_CONTAINER.getPassword(),
                "driverClassName", MYSQL_CONTAINER.getDriverClassName());
    }
}


