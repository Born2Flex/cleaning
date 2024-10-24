package org.ukma.notificationserver.jms.models;

import java.time.LocalDateTime;

public record UserEvent(Long id, String email, String name, UserEventType type, LocalDateTime creationTime) {
}
