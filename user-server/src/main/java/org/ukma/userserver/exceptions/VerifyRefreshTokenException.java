package org.ukma.userserver.exceptions;

public class VerifyRefreshTokenException extends RuntimeException {
    public VerifyRefreshTokenException(String message) {
        super(message);
    }
}
