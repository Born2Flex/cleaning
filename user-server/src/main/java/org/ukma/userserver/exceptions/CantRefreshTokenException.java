package org.ukma.userserver.exceptions;

public class CantRefreshTokenException extends RuntimeException {
    public CantRefreshTokenException(String message) {
        super(message);
    }
}
