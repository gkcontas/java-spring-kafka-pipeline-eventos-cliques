package com.gkcontas.clicks.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import com.gkcontas.clicks.dto.ClickEvent;
import com.gkcontas.clicks.messaging.ClickEventProducer;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class ClickEventGeneratorServiceTest {

    @Mock
    private ClickEventProducer clickEventProducer;

    @Test
    void shouldPublishRequestedNumberOfEvents() {
        ClickEventGeneratorService generatorService = new ClickEventGeneratorService(clickEventProducer);

        int generated = generatorService.generate(15);

        assertThat(generated).isEqualTo(15);
        verify(clickEventProducer, times(15)).publish(any(ClickEvent.class));
    }
}
