package com.gkcontas.clicks.integration;

import static org.awaitility.Awaitility.await;
import static org.hamcrest.Matchers.greaterThanOrEqualTo;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.gkcontas.clicks.dto.ClickEventRequest;
import java.time.Duration;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

class ClickEventIntegrationTest extends IntegrationTestBase {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void shouldPublishClickEventAndAggregateMetric() throws Exception {
        ClickEventRequest request = new ClickEventRequest("home", "user-42");

        mockMvc.perform(post("/events/click")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isAccepted());

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() ->
                mockMvc.perform(get("/metrics/{page}", "home"))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.length()", greaterThanOrEqualTo(1)))
                        .andExpect(jsonPath("$[0].page").value("home"))
                        .andExpect(jsonPath("$[0].totalClicks", greaterThanOrEqualTo(1)))
        );
    }

    @Test
    void shouldGenerateSyntheticEventsAndAggregateAcrossPages() throws Exception {
        mockMvc.perform(post("/events/click/generate").param("count", "10"))
                .andExpect(status().isAccepted())
                .andExpect(jsonPath("$.generated").value(10));

        await().atMost(Duration.ofSeconds(15)).untilAsserted(() -> {
            int total = 0;
            for (String page : new String[] {"home", "pricing", "docs", "blog", "contact"}) {
                String body = mockMvc.perform(get("/metrics/{page}", page))
                        .andExpect(status().isOk())
                        .andReturn().getResponse().getContentAsString();
                total += objectMapper.readTree(body).size();
            }
            org.assertj.core.api.Assertions.assertThat(total).isGreaterThan(0);
        });
    }

    @Test
    void shouldReturnBadRequestForBlankPage() throws Exception {
        ClickEventRequest request = new ClickEventRequest("", "user-1");

        mockMvc.perform(post("/events/click")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(request)))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.fieldErrors.page").exists());
    }
}
