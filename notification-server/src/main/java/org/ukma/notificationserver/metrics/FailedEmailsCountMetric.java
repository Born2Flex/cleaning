package org.ukma.notificationserver.metrics;

import io.micrometer.core.instrument.DistributionSummary;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class FailedEmailsCountMetric {
    private final DistributionSummary customCounter;

    public FailedEmailsCountMetric(MeterRegistry registry) {
        this.customCounter = DistributionSummary .builder("failed.emails.count")
                .description("Count of failed emails")
                .register(registry);
    }

    public void recordFailedEmailsCount(int count) {
        customCounter.record(count);
    }
}
