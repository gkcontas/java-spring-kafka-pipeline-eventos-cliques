package com.gkcontas.clicks.messaging;

import com.gkcontas.clicks.dto.ClickEvent;
import com.gkcontas.clicks.service.MetricAggregationService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;

@Component
public class ClickEventConsumer {

    private static final Logger LOG = LoggerFactory.getLogger(ClickEventConsumer.class);

    private final MetricAggregationService aggregationService;

    public ClickEventConsumer(MetricAggregationService aggregationService) {
        this.aggregationService = aggregationService;
    }

    @KafkaListener(topics = "${app.kafka.clicks-topic}", groupId = "${spring.kafka.consumer.group-id}")
    public void onClickEvent(ClickEvent event) {
        LOG.debug("Consuming click event for page {}", event.page());
        aggregationService.aggregate(event);
    }
}
