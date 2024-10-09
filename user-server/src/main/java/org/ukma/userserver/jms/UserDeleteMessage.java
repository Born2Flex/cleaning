package org.ukma.userserver.jms;

public record UserDeleteMessage(Long id, String email, String name) {
}
