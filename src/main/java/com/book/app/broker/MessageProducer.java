package com.book.app.broker;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

@Component
public class MessageProducer {

    private final Logger log = LoggerFactory.getLogger(MessageProducer.class);

    @Autowired
    private KafkaTemplate<String, String> kafkaTemplate;

    public void sendMessage(String topic, String message) {
        log.info("Publishing message {} to topic {}", message, topic);
        kafkaTemplate.send(topic, message);
    }
}
