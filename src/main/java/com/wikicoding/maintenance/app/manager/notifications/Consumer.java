package com.wikicoding.maintenance.app.manager.notifications;

import lombok.extern.slf4j.Slf4j;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class Consumer {
    private final String topic = "completed-tickets";

    @KafkaListener(topics = { topic }, groupId = "manager-notifications")
    public void listen(String message) {
        log.info("Ticket with id {} was completed", message);
    }
}
