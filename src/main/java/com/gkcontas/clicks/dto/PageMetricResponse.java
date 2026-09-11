package com.gkcontas.clicks.dto;

import com.gkcontas.clicks.model.PageMetric;
import java.time.Instant;

public record PageMetricResponse(String page, Instant minuteWindow, long totalClicks) {

    public static PageMetricResponse of(PageMetric metric) {
        return new PageMetricResponse(metric.getPage(), metric.getMinuteWindow(), metric.getTotalClicks());
    }
}
