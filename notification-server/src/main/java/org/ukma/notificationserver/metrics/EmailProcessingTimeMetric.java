package org.ukma.notificationserver.metrics;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Timer;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;

@Component
public class EmailProcessingTimeMetric {
    private final Timer timer;

    public EmailProcessingTimeMetric(MeterRegistry registry) {
        this.timer = Timer.builder("email.processing.time")
                .description("Duration from time when event publish to activeMQ till the time when email sent")
                .register(registry);
    }

    public void recordExecutionTime(LocalDateTime start, LocalDateTime finish) {
        timer.record(Duration.between(start, finish));
    }
}
