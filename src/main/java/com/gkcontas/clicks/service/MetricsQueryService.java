package com.gkcontas.clicks.service;

import com.gkcontas.clicks.dto.PageMetricResponse;
import com.gkcontas.clicks.repository.PageMetricRepository;
import java.util.List;
import org.springframework.stereotype.Service;

@Service
public class MetricsQueryService {

    private final PageMetricRepository pageMetricRepository;

    public MetricsQueryService(PageMetricRepository pageMetricRepository) {
        this.pageMetricRepository = pageMetricRepository;
    }

    public List<PageMetricResponse> findByPage(String page) {
        return pageMetricRepository.findByPageOrderByMinuteWindowAsc(page).stream()
                .map(PageMetricResponse::of)
                .toList();
    }
}
