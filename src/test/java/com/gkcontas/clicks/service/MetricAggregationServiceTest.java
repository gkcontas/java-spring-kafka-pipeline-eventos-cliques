package com.gkcontas.clicks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.verify;

import com.gkcontas.clicks.dto.ClickEvent;
import com.gkcontas.clicks.model.PageMetric;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;

@ExtendWith(MockitoExtension.class)
class MetricAggregationServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @Test
    void shouldUpsertMetricTruncatedToTheMinuteWindow() {
        MetricAggregationService aggregationService = new MetricAggregationService(mongoTemplate);
        Instant timestamp = Instant.parse("2026-01-01T10:15:42.123Z");
        Instant expectedWindow = timestamp.truncatedTo(ChronoUnit.MINUTES);
        String expectedId = PageMetric.buildId("home", expectedWindow);

        aggregationService.aggregate(new ClickEvent("home", "user-1", timestamp));

        ArgumentCaptor<Query> queryCaptor = ArgumentCaptor.forClass(Query.class);
        ArgumentCaptor<Update> updateCaptor = ArgumentCaptor.forClass(Update.class);
        verify(mongoTemplate).upsert(queryCaptor.capture(), updateCaptor.capture(), eq(PageMetric.class));

        assertThat(queryCaptor.getValue().getQueryObject().get("_id")).isEqualTo(expectedId);
        assertThat(updateCaptor.getValue().getUpdateObject().toString()).contains("totalClicks");
    }
}
