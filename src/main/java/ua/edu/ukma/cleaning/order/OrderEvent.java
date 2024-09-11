package ua.edu.ukma.cleaning.order;

import java.time.LocalDateTime;

public record OrderEvent(LocalDateTime creationTime, String message) {
}
