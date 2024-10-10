package ua.edu.ukma.cleaning.jms.models;

public record UserEvent(Long id, String email, String name, UserEventType type) {
}
