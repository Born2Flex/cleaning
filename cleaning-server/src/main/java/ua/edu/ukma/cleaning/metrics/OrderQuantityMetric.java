package ua.edu.ukma.cleaning.metrics;

import io.micrometer.core.instrument.Counter;
import io.micrometer.core.instrument.MeterRegistry;
import org.springframework.stereotype.Component;

@Component
public class OrderQuantityMetric {

    private final Counter orderQuantityCounter;

    public OrderQuantityMetric(MeterRegistry registry) {
        this.orderQuantityCounter = Counter.builder("order_quantity")
                .description("Custom metric counter for tracking order quantity")
                .register(registry);
    }

    public void increment() {
        this.orderQuantityCounter.increment();
    }
}
