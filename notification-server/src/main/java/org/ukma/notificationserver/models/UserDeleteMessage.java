package org.ukma.notificationserver.models;

public record UserDeleteMessage(Long id, String email, String name) {
}
