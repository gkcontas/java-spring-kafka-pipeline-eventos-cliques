package com.gkcontas.clicks.dto;

import java.time.Instant;

/**
 * Payload published to and consumed from the "clicks" Kafka topic.
 */
public record ClickEvent(String page, String userId, Instant timestamp) {
}
