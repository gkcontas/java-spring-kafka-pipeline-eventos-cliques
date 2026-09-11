package com.gkcontas.clicks.service;

import com.gkcontas.clicks.dto.ClickEvent;
import com.gkcontas.clicks.model.PageMetric;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.mongodb.core.query.Update;
import org.springframework.stereotype.Service;

/**
 * Aggregates click events by page and one-minute window, upserting the
 * running count atomically so concurrent consumers never lose an increment.
 */
@Service
public class MetricAggregationService {

    private final MongoTemplate mongoTemplate;

    public MetricAggregationService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public void aggregate(ClickEvent event) {
        Instant minuteWindow = event.timestamp().truncatedTo(ChronoUnit.MINUTES);
        String id = PageMetric.buildId(event.page(), minuteWindow);

        Query query = Query.query(Criteria.where("_id").is(id));
        Update update = new Update()
                .setOnInsert("page", event.page())
                .setOnInsert("minuteWindow", minuteWindow)
                .inc("totalClicks", 1);

        mongoTemplate.upsert(query, update, PageMetric.class);
    }
}
