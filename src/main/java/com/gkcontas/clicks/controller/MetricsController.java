package com.gkcontas.clicks.controller;

import com.gkcontas.clicks.dto.PageMetricResponse;
import com.gkcontas.clicks.service.MetricsQueryService;
import java.util.List;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/metrics")
public class MetricsController {

    private final MetricsQueryService metricsQueryService;

    public MetricsController(MetricsQueryService metricsQueryService) {
        this.metricsQueryService = metricsQueryService;
    }

    @GetMapping("/{page}")
    public List<PageMetricResponse> findByPage(@PathVariable String page) {
        return metricsQueryService.findByPage(page);
    }
}
