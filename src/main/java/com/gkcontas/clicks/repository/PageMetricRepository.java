package com.gkcontas.clicks.repository;

import com.gkcontas.clicks.model.PageMetric;
import java.util.List;
import org.springframework.data.mongodb.repository.MongoRepository;

public interface PageMetricRepository extends MongoRepository<PageMetric, String> {

    List<PageMetric> findByPageOrderByMinuteWindowAsc(String page);
}
