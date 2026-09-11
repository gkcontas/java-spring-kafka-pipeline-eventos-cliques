package com.gkcontas.clicks.controller;

import com.gkcontas.clicks.dto.ClickEvent;
import com.gkcontas.clicks.dto.ClickEventRequest;
import com.gkcontas.clicks.messaging.ClickEventProducer;
import com.gkcontas.clicks.service.ClickEventGeneratorService;
import jakarta.validation.Valid;
import java.time.Instant;
import java.util.Map;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/events/click")
public class ClickEventController {

    private final ClickEventProducer clickEventProducer;
    private final ClickEventGeneratorService clickEventGeneratorService;

    public ClickEventController(ClickEventProducer clickEventProducer, ClickEventGeneratorService clickEventGeneratorService) {
        this.clickEventProducer = clickEventProducer;
        this.clickEventGeneratorService = clickEventGeneratorService;
    }

    @PostMapping
    public ResponseEntity<Void> publish(@Valid @RequestBody ClickEventRequest request) {
        clickEventProducer.publish(new ClickEvent(request.page(), request.userId(), Instant.now()));
        return ResponseEntity.accepted().build();
    }

    @PostMapping("/generate")
    public ResponseEntity<Map<String, Integer>> generate(@RequestParam(defaultValue = "20") int count) {
        int generated = clickEventGeneratorService.generate(count);
        return ResponseEntity.accepted().body(Map.of("generated", generated));
    }
}
