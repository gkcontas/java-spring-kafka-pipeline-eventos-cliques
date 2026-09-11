package com.gkcontas.clicks.model;

import java.time.Instant;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "page_metrics")
@Getter
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PageMetric {

    @Id
    private String id;

    private String page;

    private Instant minuteWindow;

    private long totalClicks;

    public PageMetric(String page, Instant minuteWindow) {
        this.id = buildId(page, minuteWindow);
        this.page = page;
        this.minuteWindow = minuteWindow;
        this.totalClicks = 0;
    }

    public static String buildId(String page, Instant minuteWindow) {
        return page + "_" + minuteWindow;
    }
}
