package com.gkcontas.clicks.messaging;

import com.gkcontas.clicks.dto.ClickEvent;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Service;

@Service
public class ClickEventProducer {

    private final KafkaTemplate<String, ClickEvent> kafkaTemplate;
    private final String topic;

    public ClickEventProducer(
            KafkaTemplate<String, ClickEvent> kafkaTemplate,
            @Value("${app.kafka.clicks-topic}") String topic
    ) {
        this.kafkaTemplate = kafkaTemplate;
        this.topic = topic;
    }

    public void publish(ClickEvent event) {
        kafkaTemplate.send(topic, event.page(), event);
    }
}
