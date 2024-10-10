package org.ukma.userserver.jms;

import org.ukma.userserver.user.UserEntity;

public record UserEvent(Long id, String email, String name, UserEventType type) {
    public static UserEvent deleteEventFrom(UserEntity entity) {
        return new UserEvent(entity.getId(), entity.getEmail(), entity.getName(), UserEventType.DELETE);
    }

    public static UserEvent createEventFrom(UserEntity entity) {
        return new UserEvent(entity.getId(), entity.getEmail(), entity.getName(), UserEventType.CREATE);
    }
}
