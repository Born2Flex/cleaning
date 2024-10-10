package org.ukma.notificationserver.jms.models;

public record UserEvent(Long id, String email, String name, UserEventType type) {
}
