package org.ukma.userserver.jms;

import org.ukma.userserver.user.UserEntity;

import java.time.LocalDateTime;

public record UserEvent(Long id, String email, String name, UserEventType type, LocalDateTime creationTime) {
    public static UserEvent deleteEventFrom(UserEntity entity) {
        return new UserEvent(entity.getId(), entity.getEmail(), entity.getName(), UserEventType.DELETE, LocalDateTime.now());
    }

    public static UserEvent createEventFrom(UserEntity entity) {
        return new UserEvent(entity.getId(), entity.getEmail(), entity.getName(), UserEventType.CREATE, LocalDateTime.now());
    }
}
