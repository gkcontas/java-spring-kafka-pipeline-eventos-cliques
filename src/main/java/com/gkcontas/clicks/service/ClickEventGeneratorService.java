package com.gkcontas.clicks.service;

import com.gkcontas.clicks.dto.ClickEvent;
import com.gkcontas.clicks.messaging.ClickEventProducer;
import java.time.Instant;
import java.util.List;
import java.util.concurrent.ThreadLocalRandom;
import org.springframework.stereotype.Service;

/**
 * Generates synthetic click events so the pipeline can be demonstrated
 * end-to-end without needing real traffic.
 */
@Service
public class ClickEventGeneratorService {

    private static final List<String> SAMPLE_PAGES = List.of("home", "pricing", "docs", "blog", "contact");

    private final ClickEventProducer producer;

    public ClickEventGeneratorService(ClickEventProducer producer) {
        this.producer = producer;
    }

    public int generate(int count) {
        ThreadLocalRandom random = ThreadLocalRandom.current();
        for (int i = 0; i < count; i++) {
            String page = SAMPLE_PAGES.get(random.nextInt(SAMPLE_PAGES.size()));
            String userId = "user-" + random.nextInt(1000);
            producer.publish(new ClickEvent(page, userId, Instant.now()));
        }
        return count;
    }
}
