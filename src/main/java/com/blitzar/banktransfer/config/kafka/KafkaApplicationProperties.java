package com.blitzar.banktransfer.config.kafka;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "app.kafka")
public record KafkaApplicationProperties(String bankTransferTopic, String bankTransferTopicGroupId) {
}
